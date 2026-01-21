package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.GetTrackedForDayUseCase
import com.kurban.calory.features.main.domain.model.MacroTotals
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainUiState
import kotlinx.coroutines.withContext

class LoadDayMiddleware(
    private val getTrackedForDay: GetTrackedForDayUseCase,
    private val dispatchers: AppDispatchers,
    private val calculateTotals: CalculateTotalsUseCase,
) : Middleware<MainUiState, MainAction, MainEffect> {

    override suspend fun invoke(
        action: MainAction,
        state: MainUiState,
        dispatch: suspend (MainAction) -> Unit,
        emitEffect: suspend (MainEffect) -> Unit
    ) {
        if (action !is MainAction.LoadDay) return

        val result = withContext(dispatchers.io) {
            getTrackedForDay(GetTrackedForDayUseCase.Parameters(action.dayId))
        }

        when (result) {
            is AppResult.Success -> {
                val tracked = result.value.ifEmpty { emptyList() }
                val uiItems = tracked.map { it.toUi() }
                val totals: MacroTotals = calculateTotals(tracked)
                dispatch(MainAction.LoadDaySuccess(uiItems, totals))
            }
            is AppResult.Failure -> {
                emitEffect(MainEffect.Error(result.error))
                dispatch(MainAction.LoadDayFailure(result.error))
            }
        }
    }
}
