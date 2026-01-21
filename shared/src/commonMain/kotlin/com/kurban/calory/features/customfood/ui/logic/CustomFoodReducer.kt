package com.kurban.calory.features.customfood.ui.logic

import com.kurban.calory.core.ui.mvi.Reducer
import com.kurban.calory.features.customfood.domain.model.CustomFood
import com.kurban.calory.features.customfood.ui.model.CustomFoodAction
import com.kurban.calory.features.customfood.ui.model.CustomFoodUiState

fun customFoodReducer(): Reducer<CustomFoodUiState, CustomFoodAction> = { state, action ->
    when (action) {
        is CustomFoodAction.QueryChanged -> {
            val filtered = filterFoods(state.foods, action.query)
            state.copy(query = action.query, filteredFoods = filtered)
        }

        is CustomFoodAction.FoodsUpdated -> {
            val filtered = filterFoods(action.foods, state.query)
            state.copy(
                foods = action.foods,
                filteredFoods = filtered
            )
        }

        is CustomFoodAction.CreateFood -> state.copy(isSaving = true, error = null)
        CustomFoodAction.CreateFoodSuccess -> state.copy(isSaving = false)
        is CustomFoodAction.CreateFoodFailure -> state.copy(isSaving = false, error = action.error)

        is CustomFoodAction.AddToDiaryFailure -> state.copy(error = action.error)
        CustomFoodAction.AddToDiarySuccess -> state

        is CustomFoodAction.FoodsFailed -> state.copy(error = action.error)
        CustomFoodAction.ClearError -> state.copy(error = null)
        else -> state
    }
}

private fun filterFoods(foods: List<CustomFood>, query: String): List<CustomFood> {
    if (query.isBlank()) return foods
    return foods.filter { it.name.contains(query, ignoreCase = true) }
}
