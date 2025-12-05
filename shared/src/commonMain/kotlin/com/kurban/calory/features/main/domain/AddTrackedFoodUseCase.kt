package com.kurban.calory.features.main.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.main.domain.model.TrackedFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddTrackedFoodUseCase(
    private val repository: TrackedFoodRepository,
    private val foodRepository: FoodRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<AddTrackedFoodUseCase.Parameters, AddTrackedFoodUseCase.Result>(dispatcher) {

    override suspend fun execute(parameters: Parameters): Result {
        val base = foodRepository.findFood(parameters.foodName) ?: return Result.Error("Продукт не найден")
        val factor = parameters.grams / 100.0
        val now = Clock.System.now()
        val dayId = now.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

        val consumed = TrackedFood(
            id = 0L,
            foodId = base.id,
            name = base.name,
            grams = parameters.grams,
            calories = base.calories * factor,
            proteins = base.proteins * factor,
            fats = base.fats * factor,
            carbs = base.carbs * factor,
            dayId = dayId,
            timestamp = now.toEpochMilliseconds()
        )

        repository.add(consumed)
        return Result.Success(dayId)
    }

    data class Parameters(val foodName: String, val grams: Int)

    sealed class Result {
        data class Success(val dayId: String) : Result()
        data class Error(val message: String) : Result()
    }
}
