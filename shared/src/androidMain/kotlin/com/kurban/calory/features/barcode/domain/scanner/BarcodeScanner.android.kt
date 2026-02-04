package com.kurban.calory.features.barcode.domain.scanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.kurban.calory.features.barcode.domain.model.ScanResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException

actual class BarcodeScanner actual constructor() {
    
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var scanResult: CompletableDeferred<String>? = null
    private var context: Context? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var mlKitScanner: com.google.mlkit.vision.barcode.BarcodeScanner? = null
    
    actual suspend fun startScanning(): ScanResult {
        val context = this.context ?: return ScanResult.Error("Context not set")
        val lifecycleOwner = this.lifecycleOwner ?: (context as? LifecycleOwner)
            ?: return ScanResult.Error("LifecycleOwner not set")

        // Check and request permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkCameraPermission()) {
            requestCameraPermission()
            return ScanResult.PermissionDenied
        }

        release()
        val deferred = CompletableDeferred<String>()
        scanResult = deferred

        return try {
            startCameraAnalysis(context, lifecycleOwner, deferred)
            val barcode = deferred.await()
            ScanResult.Success(barcode)
        } catch (e: CancellationException) {
            ScanResult.Cancelled
        } catch (e: Exception) {
            ScanResult.Error("Scanning failed: ${e.message}", e)
        } finally {
            release()
        }
    }
    
    private fun checkCameraPermission(): Boolean {
        val context = this.context ?: return false
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun setupPermissionLauncher(launcher: ActivityResultLauncher<String>) {
        this.permissionLauncher = launcher
    }
    
    private fun startCameraAnalysis(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        scanResult: CompletableDeferred<String>
    ) {
        
        // ML Kit barcode scanner
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E
            )
            .build()
        
        mlKitScanner = BarcodeScanning.getClient(options)
        
        // Image analysis setup
        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        
        imageAnalysis?.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            BarcodeAnalyzer(mlKitScanner!!, scanResult)
        )
        
        // Camera setup
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis)
            } catch (e: Exception) {
                scanResult.completeExceptionally(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
    
    actual fun release() {
        scanResult?.cancel()
        scanResult = null
        imageAnalysis?.clearAnalyzer()
        cameraProvider?.unbindAll()
        cameraProvider = null
        imageAnalysis = null
        mlKitScanner?.close()
        mlKitScanner = null
    }
    
    actual fun isSupported(): Boolean {
        return try {
            val context = this.context ?: return false
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
        } catch (e: Exception) {
            false
        }
    }
    
    fun setContext(context: Context) {
        this.context = context
        this.lifecycleOwner = context as? LifecycleOwner
    }
    
    fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionLauncher?.launch(Manifest.permission.CAMERA)
        }
    }
}

private class BarcodeAnalyzer(
    private val scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    private val scanResult: CompletableDeferred<String>
) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
            
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { value ->
                            if (!scanResult.isCompleted) {
                                scanResult.complete(value)
                                return@addOnSuccessListener
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    if (!scanResult.isCompleted) {
                        scanResult.completeExceptionally(exception)
                    }
                }
                .addOnCompleteListener {
                    image.close()
                }
        } else {
            image.close()
        }
    }
}
