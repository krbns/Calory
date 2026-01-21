package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.ObserveTrackedForDayUseCase
import com.kurban.calory.features.main.domain.model.MacroTotals
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ObserveDayMiddleware(
    private val observeTrackedForDay: ObserveTrackedForDayUseCase,
    private val calculateTotals: CalculateTotalsUseCase,
    private val dispatchers: AppDispatchers,
    private val scope: CoroutineScope
) : Middleware<MainUiState, MainAction, MainEffect> {

    private var observeJob: Job? = null
    private var currentDayId: String? = null

    override suspend fun invoke(
        action: MainAction,
        state: MainUiState,
        dispatch: suspend (MainAction) -> Unit,
        emitEffect: suspend (MainEffect) -> Unit
    ) {
        if (action !is MainAction.LoadDay) return

        if (currentDayId == action.dayId && observeJob?.isActive == true) return
        currentDayId = action.dayId

        observeJob?.cancel()
        observeJob = scope.launch {
            observeTrackedForDay(action.dayId)
                .catch {
                    val error = DomainError.fromThrowable(it)
                    emitEffect(MainEffect.Error(error))
                    dispatch(MainAction.LoadDayFailure(error))
                }
                .collect { tracked ->
                    val totals: MacroTotals = withContext(dispatchers.default) { calculateTotals(tracked) }
                    dispatch(MainAction.LoadDaySuccess(tracked.map { it.toUi() }, totals))
                }
        }
    }
}
