package com.kurban.calory.features.main.ui.logic

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

        try {
            deleteTrackedFood(DeleteTrackedFoodUseCase.Parameters(action.entryId))
            val dayToReload = state.selectedDayId.takeUnless { it.isBlank() } ?: dayProvider.currentDayId()
            dispatch(MainAction.LoadDay(dayToReload))
        } catch (e: Exception) {
            emitEffect(MainEffect.Error(e.message ?: "Не удалось удалить запись"))
            dispatch(MainAction.RemoveEntryFailure(e.message.orEmpty()))
        }
    }
}
