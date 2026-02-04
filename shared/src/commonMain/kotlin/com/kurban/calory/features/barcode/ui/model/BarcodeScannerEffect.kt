package com.kurban.calory.features.barcode.ui.model

import com.kurban.calory.features.barcode.domain.model.BarcodeProduct

sealed class BarcodeScannerEffect {
    data class ShowError(val message: String) : BarcodeScannerEffect()
    object NavigateBack : BarcodeScannerEffect()
    data class NavigateToAddToDiary(val product: BarcodeProduct) : BarcodeScannerEffect()
    data class NavigateToProductNotFound(val barcode: String) : BarcodeScannerEffect()
}