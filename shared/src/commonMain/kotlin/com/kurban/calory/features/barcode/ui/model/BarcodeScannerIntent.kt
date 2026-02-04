package com.kurban.calory.features.barcode.ui.model

import com.kurban.calory.features.barcode.domain.model.BarcodeProduct

sealed class BarcodeScannerIntent {
    object StartScanning : BarcodeScannerIntent()
    object StopScanning : BarcodeScannerIntent()
    data class ProductScanned(val barcode: String) : BarcodeScannerIntent()
    data class SearchProduct(val barcode: String) : BarcodeScannerIntent()
    object ClearError : BarcodeScannerIntent()
    object NavigateBack : BarcodeScannerIntent()
    data class AddToDiary(val product: BarcodeProduct, val grams: Int) : BarcodeScannerIntent()
    data class ToggleFavorite(val barcode: String, val isFavorite: Boolean) : BarcodeScannerIntent()
    object RequestCameraPermission : BarcodeScannerIntent()
}
