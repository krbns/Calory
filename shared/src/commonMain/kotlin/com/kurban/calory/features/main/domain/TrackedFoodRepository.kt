package com.kurban.calory.features.main.domain

import com.kurban.calory.features.main.domain.model.TrackedFood

interface TrackedFoodRepository {
    suspend fun add(food: TrackedFood)
    suspend fun getByDay(dayId: String): List<TrackedFood>
    suspend fun delete(id: Long)
}
