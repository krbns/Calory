package com.kurban.calory.features.barcode.domain.model

sealed class ScanResult {
    data class Success(val barcode: String) : ScanResult()
    data class Error(val message: String, val exception: Throwable? = null) : ScanResult()
    object Cancelled : ScanResult()
    object NotSupported : ScanResult()
    object PermissionDenied : ScanResult()
}

sealed class BarcodeSearchResult {
    data class Success(val product: BarcodeProduct, val source: DataSource) : BarcodeSearchResult()
    data class NotFound(val barcode: String) : BarcodeSearchResult()
    data class Error(val barcode: String, val error: Throwable) : BarcodeSearchResult()
}

enum class DataSource {
    CACHE,
    REMOTE
}