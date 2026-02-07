package com.kurban.calory.features.barcode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import com.kurban.calory.features.barcode.domain.scanner.BarcodeScanner
import org.koin.core.context.GlobalContext

@Composable
actual fun PlatformCameraPreview(modifier: Modifier) {
    val barcodeScanner = remember { GlobalContext.get().get<BarcodeScanner>() }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
                barcodeScanner.setPreviewView(this)
            }
        },
        update = { previewView ->
            barcodeScanner.setPreviewView(previewView)
        }
    )

    DisposableEffect(barcodeScanner) {
        onDispose {
            barcodeScanner.setPreviewView(null)
        }
    }
}
