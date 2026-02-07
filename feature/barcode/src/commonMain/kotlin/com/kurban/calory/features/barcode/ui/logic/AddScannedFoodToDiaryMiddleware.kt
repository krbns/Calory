package com.kurban.calory.features.barcode.ui.logic

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.features.barcode.domain.usecase.AddScannedFoodToDiaryUseCase
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerAction
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerEffect
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerUiState

class AddScannedFoodToDiaryMiddleware(
    private val addScannedFoodToDiaryUseCase: AddScannedFoodToDiaryUseCase
) : com.kurban.calory.core.ui.mvi.Middleware<BarcodeScannerUiState, BarcodeScannerAction, BarcodeScannerEffect> {

    override suspend fun invoke(
        action: BarcodeScannerAction,
        state: BarcodeScannerUiState,
        dispatch: suspend (BarcodeScannerAction) -> Unit,
        emitEffect: suspend (BarcodeScannerEffect) -> Unit
    ) {
        if (action !is BarcodeScannerAction.AddToDiary) return

        when (val result = addScannedFoodToDiaryUseCase(AddScannedFoodToDiaryUseCase.Params(action.product, action.grams))) {
            is AppResult.Success -> {
                dispatch(BarcodeScannerAction.AddToDiarySuccess)
                emitEffect(BarcodeScannerEffect.NavigateBack)
            }
            is AppResult.Failure -> {
                val message = result.error.message ?: "Failed to add to diary"
                dispatch(BarcodeScannerAction.AddToDiaryFailure(message))
                emitEffect(BarcodeScannerEffect.ShowError(message))
            }
        }
    }
}
