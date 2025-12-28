package com.kurban.calory.features.main.ui.model

import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.profile.domain.model.MacroTargets

data class MainUiState(
    val query: String = "",
    val gramsInput: String = "100",
    val searchResults: List<Food> = emptyList(),
    val isSearching: Boolean = false,
    val selectedFood: Food? = null,
    val todayId: String = "",
    val selectedDayId: String = "",
    val days: List<UIDay> = emptyList(),
    val tracked: List<UITrackedFood> = emptyList(),
    val totalCalories: Double = 0.0,
    val totalProteins: Double = 0.0,
    val totalFats: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val macroTargets: MacroTargets? = null
)
