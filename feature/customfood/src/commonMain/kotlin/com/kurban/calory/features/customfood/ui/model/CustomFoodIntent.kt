package com.kurban.calory.features.customfood.ui.model

sealed class CustomFoodIntent {
    object Load : CustomFoodIntent()
    data class QueryChanged(val query: String) : CustomFoodIntent()
    data class CreateFood(
        val name: String,
        val calories: String,
        val proteins: String,
        val fats: String,
        val carbs: String
    ) : CustomFoodIntent()

    data class AddToDiary(val foodId: Long, val grams: Int) : CustomFoodIntent()
    object ClearError : CustomFoodIntent()
}
