package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
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

        try {
            val tracked = withContext(dispatchers.io) {
                getTrackedForDay(GetTrackedForDayUseCase.Parameters(action.dayId)) ?: emptyList()
            }
            val uiItems = tracked.map { it.toUi() }
            val totals: MacroTotals = calculateTotals(tracked)
            dispatch(MainAction.LoadDaySuccess(uiItems, totals))
        } catch (e: Exception) {
            emitEffect(MainEffect.Error(e.message ?: "Не удалось загрузить данные"))
            dispatch(MainAction.LoadDayFailure(e.message.orEmpty()))
        }
    }
}
