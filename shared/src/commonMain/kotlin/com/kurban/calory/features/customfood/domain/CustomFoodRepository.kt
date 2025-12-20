package com.kurban.calory.features.customfood.domain

import com.kurban.calory.features.customfood.domain.model.CustomFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface CustomFoodRepository {

    fun observeAll(dispatcher: CoroutineDispatcher): Flow<List<CustomFood>>

    suspend fun add(food: NewCustomFood): CustomFood

    suspend fun getById(id: Long): CustomFood?
}

data class NewCustomFood(
    val name: String,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double
)
