package com.kurban.calory.features.customfood.ui.model

sealed class CustomFoodEffect {
    data class Error(val message: String) : CustomFoodEffect()
    data class FoodCreated(val name: String) : CustomFoodEffect()
    data class AddedToDiary(val name: String) : CustomFoodEffect()
}
