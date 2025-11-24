package com.kurban.calory.features.main.domain.model

data class TrackedFood(
    val entryId: Long,
    val foodId: Long,
    val name: String,
    val grams: Int,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
)
