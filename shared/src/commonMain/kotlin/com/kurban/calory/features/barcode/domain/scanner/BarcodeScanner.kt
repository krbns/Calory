package com.kurban.calory.features.barcode.domain.scanner

import com.kurban.calory.features.barcode.domain.model.ScanResult

/**
 * Platform-specific barcode scanner interface
 */
expect class BarcodeScanner() {
    
    /**
     * Starts barcode scanning and returns the result
     * 
     * @return ScanResult containing the scanned barcode or error
     */
    suspend fun startScanning(): ScanResult
    
    /**
     * Checks if barcode scanning is supported on the current platform
     */
    fun isSupported(): Boolean
    
    /**
     * Releases scanner resources
     */
    fun release()
}

/**
 * Factory for creating platform-specific barcode scanner instances
 */
object BarcodeScannerFactory {
    fun create(): BarcodeScanner = BarcodeScanner()
}