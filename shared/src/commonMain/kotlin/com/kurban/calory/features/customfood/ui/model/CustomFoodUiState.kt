package com.kurban.calory.features.customfood.ui.model

import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.features.customfood.domain.model.CustomFood

data class CustomFoodUiState(
    val query: String = "",
    val foods: List<CustomFood> = emptyList(),
    val filteredFoods: List<CustomFood> = emptyList(),
    val isSaving: Boolean = false,
    val error: DomainError? = null
)
