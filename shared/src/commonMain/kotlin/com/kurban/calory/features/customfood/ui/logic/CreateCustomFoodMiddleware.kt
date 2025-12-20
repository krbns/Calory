package com.kurban.calory.features.customfood.ui.logic

import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.customfood.domain.CreateCustomFoodUseCase
import com.kurban.calory.features.customfood.ui.model.CustomFoodAction
import com.kurban.calory.features.customfood.ui.model.CustomFoodEffect
import com.kurban.calory.features.customfood.ui.model.CustomFoodUiState

class CreateCustomFoodMiddleware(
    private val createCustomFoodUseCase: CreateCustomFoodUseCase
) : Middleware<CustomFoodUiState, CustomFoodAction, CustomFoodEffect> {

    override suspend fun invoke(
        action: CustomFoodAction,
        state: CustomFoodUiState,
        dispatch: suspend (CustomFoodAction) -> Unit,
        emitEffect: suspend (CustomFoodEffect) -> Unit
    ) {
        if (action !is CustomFoodAction.CreateFood) return

        when (val result = createCustomFoodUseCase(action.parameters)) {
            is CreateCustomFoodUseCase.Result.Success -> {
                dispatch(CustomFoodAction.CreateFoodSuccess)
                emitEffect(CustomFoodEffect.FoodCreated(result.food.name))
            }

            is CreateCustomFoodUseCase.Result.Error -> {
                dispatch(CustomFoodAction.CreateFoodFailure(result.message))
                emitEffect(CustomFoodEffect.Error(result.message))
            }

            null -> {
                val message = "Не удалось сохранить продукт"
                dispatch(CustomFoodAction.CreateFoodFailure(message))
                emitEffect(CustomFoodEffect.Error(message))
            }
        }
    }
}
