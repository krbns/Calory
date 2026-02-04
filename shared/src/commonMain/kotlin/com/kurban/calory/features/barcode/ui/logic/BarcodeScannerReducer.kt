package com.kurban.calory.features.barcode.ui.logic

import com.kurban.calory.core.ui.mvi.Reducer
import com.kurban.calory.features.barcode.domain.model.BarcodeSearchResult
import com.kurban.calory.features.barcode.domain.model.ScanResult
import com.kurban.calory.features.barcode.ui.model.BarcodeProductSearchResult
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerAction
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerEffect
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerUiState

fun barcodeScannerReducer(): Reducer<BarcodeScannerUiState, BarcodeScannerAction?> = { state, action ->
    when (action) {
        is BarcodeScannerAction.StartScanning -> {
            state.copy(
                isScanning = true,
                scanResult = null,
                productResult = null,
                error = null,
                isLoading = false
            )
        }

        is BarcodeScannerAction.StopScanning -> {
            state.copy(
                isScanning = false,
                isLoading = false
            )
        }

        is BarcodeScannerAction.ScanCompleted -> {
            when (action.result) {
                is ScanResult.Success -> {
                    state.copy(
                        isScanning = false,
                        scanResult = action.result,
                        isLoading = false,
                        error = null
                    )
                }

                is ScanResult.Error -> {
                    state.copy(
                        isScanning = false,
                        scanResult = action.result,
                        isLoading = false,
                        error = action.result.message
                    )
                }

                is ScanResult.Cancelled -> {
                    state.copy(
                        isScanning = false,
                        isLoading = false
                    )
                }

                is ScanResult.NotSupported -> {
                    state.copy(
                        isScanning = false,
                        isLoading = false,
                        isSupported = false,
                        error = "Barcode scanning is not supported on this device"
                    )
                }
                
                is ScanResult.PermissionDenied -> {
                    state.copy(
                        isScanning = false,
                        isLoading = false,
                        error = "Camera permission is required to scan barcodes"
                    )
                }
            }
        }

        is BarcodeScannerAction.SearchProduct -> {
            state.copy(
                isLoading = true,
                productResult = BarcodeProductSearchResult.Loading,
                error = null
            )
        }
        
        is BarcodeScannerAction.ProductSearchResult -> {
            when (action.result) {
                is BarcodeSearchResult.Success -> {
                    state.copy(
                        isLoading = false,
                        productResult = BarcodeProductSearchResult.Success(action.result.product),
                        error = null
                    )
                }
                
                is BarcodeSearchResult.NotFound -> {
                    state.copy(
                        isLoading = false,
                        productResult = BarcodeProductSearchResult.NotFound(action.result.barcode),
                        error = null
                    )
                }
                
                is BarcodeSearchResult.Error -> {
                    state.copy(
                        isLoading = false,
                        productResult = BarcodeProductSearchResult.Error(
                            action.result.error.message ?: "Unknown error"
                        ),
                        error = action.result.error.message
                    )
                }
            }
        }

        is BarcodeScannerAction.ClearError -> {
            state.copy(error = null)
        }

        is BarcodeScannerAction.NavigateBack -> {
            state
        }

        is BarcodeScannerAction.AddToDiary -> {
            state.copy(isLoading = true)
        }
        
        is BarcodeScannerAction.AddToDiarySuccess -> {
            state.copy(isLoading = false)
        }
        
        is BarcodeScannerAction.AddToDiaryFailure -> {
            state.copy(isLoading = false, error = action.message)
        }
        
        is BarcodeScannerAction.ToggleFavorite -> {
            state // State updated through middleware effects
        }
        
        else -> state
    }
}

fun handleProductSearchResult(
    state: BarcodeScannerUiState,
    result: BarcodeSearchResult
): Pair<BarcodeScannerUiState, BarcodeScannerEffect?> {
    return when (result) {
        is BarcodeSearchResult.Success -> {
            state.copy(
                isLoading = false,
                productResult = BarcodeProductSearchResult.Success(result.product),
                error = null
            ) to BarcodeScannerEffect.NavigateToAddToDiary(result.product)
        }

        is BarcodeSearchResult.NotFound -> {
            state.copy(
                isLoading = false,
                productResult = BarcodeProductSearchResult.NotFound(result.barcode),
                error = null
            ) to BarcodeScannerEffect.NavigateToProductNotFound(result.barcode)
        }

        is BarcodeSearchResult.Error -> {
            state.copy(
                isLoading = false,
                productResult = BarcodeProductSearchResult.Error(
                    result.error.message ?: "Unknown error"
                ),
                error = result.error.message
            ) to BarcodeScannerEffect.ShowError(result.error.message ?: "Unknown error")
        }
    }
}
