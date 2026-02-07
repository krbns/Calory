package com.kurban.calory.features.customfood.ui.logic

import com.kurban.calory.core.domain.AppResult
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

        val result = createCustomFoodUseCase(action.parameters)

        when (result) {
            is AppResult.Success -> {
                dispatch(CustomFoodAction.CreateFoodSuccess)
                emitEffect(CustomFoodEffect.FoodCreated(result.value.name))
            }

            is AppResult.Failure -> {
                dispatch(CustomFoodAction.CreateFoodFailure(result.error))
                emitEffect(CustomFoodEffect.Error(result.error))
            }
        }
    }
}
