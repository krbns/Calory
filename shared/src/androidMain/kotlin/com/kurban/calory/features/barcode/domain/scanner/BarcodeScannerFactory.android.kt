package com.kurban.calory.features.barcode.domain.scanner

actual fun BarcodeScannerFactory(): BarcodeScanner {
    return BarcodeScanner()
}