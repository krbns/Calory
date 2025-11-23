package com.kurban.calory.features.main.domain.model

//TODO разделить на два разных модели, одно которое получаем из базы данных,
// а второе для сохранения в базу сьеденных продуктов

data class Food(
    val id: Long,
    val grams: Int,
    val name: String,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double,
)