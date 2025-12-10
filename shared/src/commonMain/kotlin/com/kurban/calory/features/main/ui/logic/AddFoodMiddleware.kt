package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.mvi.Middleware
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

        val selected = state.selectedFood ?: run {
            emitEffect(MainEffect.Error("Сначала выберите продукт"))
            return
        }
        val gramsValue = state.gramsInput.replace(',', '.').toDoubleOrNull()?.roundToInt() ?: run {
            emitEffect(MainEffect.Error("Введите размер порции в граммах"))
            return
        }

        try {
            when (val result = addTrackedFoodUseCase(AddTrackedFoodUseCase.Parameters(selected.name, gramsValue))) {
                is AddTrackedFoodUseCase.Result.Success -> dispatch(MainAction.LoadDay(result.dayId))
                is AddTrackedFoodUseCase.Result.Error -> emitEffect(MainEffect.Error(result.message))
                null -> emitEffect(MainEffect.Error("Не удалось добавить продукт"))
            }
        } catch (e: Exception) {
            emitEffect(MainEffect.Error(e.message ?: "Не удалось добавить продукт"))
            dispatch(MainAction.AddFoodFailure(e.message.orEmpty()))
        }
    }
}
