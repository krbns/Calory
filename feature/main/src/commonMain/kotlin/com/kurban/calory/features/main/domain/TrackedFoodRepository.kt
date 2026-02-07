package com.kurban.calory.features.main.domain

import com.kurban.calory.features.main.domain.model.TrackedFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface TrackedFoodRepository {
    suspend fun add(food: TrackedFood)
    suspend fun getByDay(dayId: String): List<TrackedFood>
    fun observeByDay(dayId: String, dispatcher: CoroutineDispatcher): Flow<List<TrackedFood>>
    suspend fun delete(id: Long)
}
