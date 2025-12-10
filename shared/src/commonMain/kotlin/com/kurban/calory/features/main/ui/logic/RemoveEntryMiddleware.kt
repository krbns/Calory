package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.mvi.Middleware
import com.kurban.calory.core.time.DayProvider
import com.kurban.calory.features.main.domain.DeleteConsumedFoodUseCase
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainUiState

class RemoveEntryMiddleware(
    private val deleteTrackedFood: DeleteConsumedFoodUseCase,
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
            deleteTrackedFood(DeleteConsumedFoodUseCase.Parameters(action.entryId))
            dispatch(MainAction.LoadDay(dayProvider.currentDayId()))
        } catch (e: Exception) {
            emitEffect(MainEffect.Error(e.message ?: "Не удалось удалить запись"))
            dispatch(MainAction.RemoveEntryFailure(e.message.orEmpty()))
        }
    }
}
