package com.kurban.calory.features.barcode.ui.model

import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.barcode.domain.model.ScanResult

data class BarcodeScannerUiState(
    val isScanning: Boolean = false,
    val isSupported: Boolean = true,
    val scanResult: ScanResult? = null,
    val productResult: BarcodeProductSearchResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class BarcodeProductSearchResult {
    data class Success(val product: BarcodeProduct) : BarcodeProductSearchResult()
    data class NotFound(val barcode: String) : BarcodeProductSearchResult()
    data class Error(val message: String) : BarcodeProductSearchResult()
    object Loading : BarcodeProductSearchResult()
}