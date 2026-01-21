package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.main.domain.DeleteTrackedFoodUseCase
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainUiState

class RemoveEntryMiddleware(
    private val deleteTrackedFood: DeleteTrackedFoodUseCase,
    private val dayProvider: DayProvider
) : Middleware<MainUiState, MainAction, MainEffect> {

    override suspend fun invoke(
        action: MainAction,
        state: MainUiState,
        dispatch: suspend (MainAction) -> Unit,
        emitEffect: suspend (MainEffect) -> Unit
    ) {
        if (action !is MainAction.RemoveEntry) return

        val result = deleteTrackedFood(DeleteTrackedFoodUseCase.Parameters(action.entryId))

        when (result) {
            is AppResult.Success -> {
                val dayToReload = state.selectedDayId.takeUnless { it.isBlank() } ?: dayProvider.currentDayId()
                dispatch(MainAction.LoadDay(dayToReload))
            }
            is AppResult.Failure -> {
                emitEffect(MainEffect.Error(result.error))
                dispatch(MainAction.RemoveEntryFailure(result.error))
            }
        }
    }
}
