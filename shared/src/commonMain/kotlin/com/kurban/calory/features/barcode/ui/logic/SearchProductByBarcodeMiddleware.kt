package com.kurban.calory.features.barcode.ui.logic

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.features.barcode.domain.model.BarcodeSearchResult
import com.kurban.calory.features.barcode.domain.usecase.SearchProductByBarcodeUseCase
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerAction
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerEffect
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerUiState

class SearchProductByBarcodeMiddleware(
    private val searchProductByBarcodeUseCase: SearchProductByBarcodeUseCase
) : com.kurban.calory.core.ui.mvi.Middleware<BarcodeScannerUiState, BarcodeScannerAction, BarcodeScannerEffect> {

    override suspend fun invoke(
        action: BarcodeScannerAction,
        state: BarcodeScannerUiState,
        dispatch: suspend (BarcodeScannerAction) -> Unit,
        emitEffect: suspend (BarcodeScannerEffect) -> Unit
    ) {
        if (action !is BarcodeScannerAction.SearchProduct) return

        when (val result = searchProductByBarcodeUseCase(SearchProductByBarcodeUseCase.Params(action.barcode))) {
            is AppResult.Success -> {
                val searchResult = result.value
                dispatch(BarcodeScannerAction.ProductSearchResult(searchResult))
                if (searchResult is BarcodeSearchResult.Error) {
                    emitEffect(
                        BarcodeScannerEffect.ShowError(
                            searchResult.error.message ?: "Unknown error"
                        )
                    )
                }
            }
            
            is AppResult.Failure -> {
                val throwable = result.error.cause ?: Exception(result.error.message ?: "Search failed")
                dispatch(
                    BarcodeScannerAction.ProductSearchResult(
                        BarcodeSearchResult.Error(action.barcode, throwable)
                    )
                )
                emitEffect(BarcodeScannerEffect.ShowError(result.error.message ?: "Failed to search product"))
            }
        }
    }
}
