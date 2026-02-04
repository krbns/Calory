package com.kurban.calory.features.barcode.ui.logic

import com.kurban.calory.features.barcode.domain.usecase.ToggleFavoriteUseCase
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerAction
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerEffect
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerUiState
import com.kurban.calory.core.domain.AppResult

class ToggleFavoriteMiddleware(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : com.kurban.calory.core.ui.mvi.Middleware<BarcodeScannerUiState, BarcodeScannerAction, BarcodeScannerEffect> {

    override suspend fun invoke(
        action: BarcodeScannerAction,
        state: BarcodeScannerUiState,
        dispatch: suspend (BarcodeScannerAction) -> Unit,
        emitEffect: suspend (BarcodeScannerEffect) -> Unit
    ) {
        if (action !is BarcodeScannerAction.ToggleFavorite) return

        when (val result = toggleFavoriteUseCase(ToggleFavoriteUseCase.Params(action.barcode, action.isFavorite))) {
            is AppResult.Success -> Unit
            is AppResult.Failure -> emitEffect(
                BarcodeScannerEffect.ShowError(
                    result.error.message ?: "Failed to toggle favorite"
                )
            )
        }
    }
}
