package com.kurban.calory.features.main.domain

import com.kurban.calory.features.main.domain.model.Food

interface FoodRepository {
    fun findFood(name: String): Food?

    fun search(query: String): List<Food>

    fun addUserFood(name: String, grams: Int): Food?
}