package com.kurban.calory.features.barcode.domain.scanner

import com.kurban.calory.features.barcode.domain.model.ScanResult

actual class BarcodeScanner {
    
    actual fun isSupported(): Boolean {
        return false
    }
    
    actual suspend fun startScanning(): ScanResult {
        return ScanResult.NotSupported
    }
    
    actual fun release() {
        // No-op for desktop
    }
}