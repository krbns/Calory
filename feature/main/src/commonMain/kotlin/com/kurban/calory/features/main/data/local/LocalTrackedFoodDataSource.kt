package com.kurban.calory.features.main.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kurban.calory.features.main.data.TrackedFoodDataSource
import com.kurban.calory.features.main.domain.model.TrackedFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override fun observeByDay(dayId: String, dispatcher: CoroutineDispatcher): Flow<List<TrackedFood>> {
        return database.trackedFoodQueries.selectTrackedByDay(dayId)
            .asFlow()
            .mapToList(dispatcher)
            .map { list ->
                list.map {
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
    }

    override suspend fun delete(id: Long) {
        database.trackedFoodQueries.deleteTracked(id)
    }
}
