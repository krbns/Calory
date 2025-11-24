package com.kurban.calory.features.main.ui

import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.main.domain.model.TrackedFood

data class MainUiState(
    val query: String = "",
    val gramsInput: String = "100",
    val searchResults: List<Food> = emptyList(),
    val isSearching: Boolean = false,
    val selectedFood: Food? = null,
    val consumed: List<TrackedFood> = emptyList(),
    val totalCalories: Double = 0.0,
    val totalProteins: Double = 0.0,
    val totalFats: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val error: String? = null
)
