package com.kurban.calory.features.customfood.ui.logic

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.customfood.domain.AddCustomFoodToDiaryUseCase
import com.kurban.calory.features.customfood.ui.model.CustomFoodAction
import com.kurban.calory.features.customfood.ui.model.CustomFoodEffect
import com.kurban.calory.features.customfood.ui.model.CustomFoodUiState

class AddCustomFoodToDiaryMiddleware(
    private val addCustomFoodToDiary: AddCustomFoodToDiaryUseCase
) : Middleware<CustomFoodUiState, CustomFoodAction, CustomFoodEffect> {

    override suspend fun invoke(
        action: CustomFoodAction,
        state: CustomFoodUiState,
        dispatch: suspend (CustomFoodAction) -> Unit,
        emitEffect: suspend (CustomFoodEffect) -> Unit
    ) {
        if (action !is CustomFoodAction.AddToDiary) return

        val result = addCustomFoodToDiary(AddCustomFoodToDiaryUseCase.Parameters(action.foodId, action.grams))

        when (result) {
            is AppResult.Success -> {
                dispatch(CustomFoodAction.AddToDiarySuccess)
                val name = state.foods.firstOrNull { it.id == action.foodId }?.name
                if (name != null) {
                    emitEffect(CustomFoodEffect.AddedToDiary(name))
                }
            }

            is AppResult.Failure -> {
                dispatch(CustomFoodAction.AddToDiaryFailure(result.error))
                emitEffect(CustomFoodEffect.Error(result.error))
            }
        }
    }
}
