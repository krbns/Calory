package com.kurban.calory.features.customfood.domain.model

data class CustomFood(
    val id: Long,
    val name: String,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double
)
