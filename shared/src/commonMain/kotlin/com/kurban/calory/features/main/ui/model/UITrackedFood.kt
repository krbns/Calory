package com.kurban.calory.features.main.ui.model

data class UITrackedFood(
    val entryId: Long,
    val foodId: Long,
    val name: String,
    val grams: Int,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
)