package com.kurban.calory.features.customfood.ui.model

import com.kurban.calory.core.domain.DomainError

sealed class CustomFoodEffect {
    data class Error(val error: DomainError) : CustomFoodEffect()
    data class FoodCreated(val name: String) : CustomFoodEffect()
    data class AddedToDiary(val name: String) : CustomFoodEffect()
}
