package com.kurban.calory.features.main.data

import com.kurban.calory.features.main.domain.TrackedFoodRepository
import com.kurban.calory.features.main.domain.model.TrackedFood

class DefaultTrackedFoodRepository(
    private val dataSource: TrackedFoodDataSource
) : TrackedFoodRepository {
    override suspend fun add(food: TrackedFood) {
        dataSource.insert(food)
    }

    override suspend fun getByDay(dayId: String): List<TrackedFood> {
        return dataSource.getByDay(dayId)
    }

    override suspend fun delete(id: Long) {
        dataSource.delete(id)
    }
}
