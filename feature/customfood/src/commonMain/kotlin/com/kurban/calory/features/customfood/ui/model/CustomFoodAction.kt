package com.kurban.calory.features.customfood.ui.model

import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.features.customfood.domain.CreateCustomFoodUseCase
import com.kurban.calory.features.customfood.domain.model.CustomFood

sealed class CustomFoodAction {
    object ObserveFoods : CustomFoodAction()
    data class FoodsUpdated(val foods: List<CustomFood>) : CustomFoodAction()
    data class FoodsFailed(val error: DomainError) : CustomFoodAction()

    data class QueryChanged(val query: String) : CustomFoodAction()
    data class CreateFood(val parameters: CreateCustomFoodUseCase.Parameters) : CustomFoodAction()
    data class CreateFoodFailure(val error: DomainError) : CustomFoodAction()
    object CreateFoodSuccess : CustomFoodAction()

    data class AddToDiary(val foodId: Long, val grams: Int) : CustomFoodAction()
    data class AddToDiaryFailure(val error: DomainError) : CustomFoodAction()
    object AddToDiarySuccess : CustomFoodAction()

    object ClearError : CustomFoodAction()
}
