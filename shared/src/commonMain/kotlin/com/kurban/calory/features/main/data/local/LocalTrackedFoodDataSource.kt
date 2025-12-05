package com.kurban.calory.features.main.data.local

import com.kurban.calory.features.main.data.TrackedFoodDataSource
import com.kurban.calory.features.main.domain.model.TrackedFood
import sqldelight.trackedFoodScheme.tracked.TrackedFoodDatabase

class LocalTrackedFoodDataSource(
    private val database: TrackedFoodDatabase
) : TrackedFoodDataSource {

    override suspend fun insert(food: TrackedFood) {
        database.trackedFoodQueries.insertTrackedFood(
            foodId = food.foodId,
            name = food.name,
            grams = food.grams.toLong(),
            calories = food.calories,
            proteins = food.proteins,
            fats = food.fats,
            carbs = food.carbs,
            dayId = food.dayId,
            timestamp = food.timestamp
        )
    }

    override suspend fun getByDay(dayId: String): List<TrackedFood> {
        return database.trackedFoodQueries.selectTrackedByDay(dayId).executeAsList().map {
            TrackedFood(
                id = it.id,
                foodId = it.foodId,
                name = it.name,
                grams = it.grams.toInt(),
                calories = it.calories,
                proteins = it.proteins,
                fats = it.fats,
                carbs = it.carbs,
                dayId = it.dayId,
                timestamp = it.timestamp
            )
        }
    }

    override suspend fun delete(id: Long) {
        database.trackedFoodQueries.deleteTracked(id)
    }
}
