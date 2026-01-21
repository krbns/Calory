package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainUiState
import kotlin.math.roundToInt

class AddFoodMiddleware(
    private val addTrackedFoodUseCase: AddTrackedFoodUseCase
) : Middleware<MainUiState, MainAction, MainEffect> {

    override suspend fun invoke(
        action: MainAction,
        state: MainUiState,
        dispatch: suspend (MainAction) -> Unit,
        emitEffect: suspend (MainEffect) -> Unit
    ) {
        if (action !is MainAction.AddSelectedFood) return

        if (state.selectedDayId.isNotBlank() && state.todayId.isNotBlank() && state.selectedDayId != state.todayId) {
            val error = DomainError.ValidationError(originalMessage = "Добавлять продукты можно только за сегодняшний день")
            emitEffect(MainEffect.Error(error))
            return
        }

        val selected = state.selectedFood ?: run {
            val error = DomainError.ValidationError(originalMessage = "Сначала выберите продукт")
            emitEffect(MainEffect.Error(error))
            return
        }
        val gramsValue = state.gramsInput.replace(',', '.').toDoubleOrNull()?.roundToInt() ?: run {
            val error = DomainError.ValidationError(originalMessage = "Введите размер порции в граммах")
            emitEffect(MainEffect.Error(error))
            return
        }

        val result = addTrackedFoodUseCase(AddTrackedFoodUseCase.Parameters(selected.name, gramsValue))

        when (result) {
            is AppResult.Success -> dispatch(MainAction.LoadDay(result.value))
            is AppResult.Failure -> {
                emitEffect(MainEffect.Error(result.error))
                dispatch(MainAction.AddFoodFailure(result.error))
            }
        }
    }
}
