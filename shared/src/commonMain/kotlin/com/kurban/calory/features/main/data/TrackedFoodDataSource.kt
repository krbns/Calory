package com.kurban.calory.features.main.data

import com.kurban.calory.features.main.domain.model.TrackedFood

interface TrackedFoodDataSource {
    suspend fun insert(food: TrackedFood)
    suspend fun getByDay(dayId: String): List<TrackedFood>
    suspend fun delete(id: Long)
}
