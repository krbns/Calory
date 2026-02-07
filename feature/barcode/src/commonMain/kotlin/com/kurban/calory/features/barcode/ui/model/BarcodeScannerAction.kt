package com.kurban.calory.features.barcode.ui.model

import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.barcode.domain.model.BarcodeSearchResult
import com.kurban.calory.features.barcode.domain.model.ScanResult

sealed class BarcodeScannerAction {
    object StartScanning : BarcodeScannerAction()
    object StopScanning : BarcodeScannerAction()
    data class ScanCompleted(val result: ScanResult) : BarcodeScannerAction()
    data class SearchProduct(val barcode: String) : BarcodeScannerAction()
    data class ProductSearchResult(val result: BarcodeSearchResult) : BarcodeScannerAction()
    object ClearError : BarcodeScannerAction()
    object NavigateBack : BarcodeScannerAction()
    data class AddToDiary(val product: BarcodeProduct, val grams: Int) : BarcodeScannerAction()
    object AddToDiarySuccess : BarcodeScannerAction()
    data class AddToDiaryFailure(val message: String) : BarcodeScannerAction()
    data class ToggleFavorite(val barcode: String, val isFavorite: Boolean) : BarcodeScannerAction()
    object RequestCameraPermission : BarcodeScannerAction()
}
