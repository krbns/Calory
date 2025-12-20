package com.kurban.calory.features.customfood.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.main.domain.TrackedFoodRepository
import com.kurban.calory.features.main.domain.model.TrackedFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.Clock

class AddCustomFoodToDiaryUseCase(
    private val customFoodRepository: CustomFoodRepository,
    private val trackedFoodRepository: TrackedFoodRepository,
    private val dayProvider: DayProvider,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<AddCustomFoodToDiaryUseCase.Parameters, AddCustomFoodToDiaryUseCase.Result>(dispatcher) {

    override suspend fun execute(parameters: Parameters): Result {
        val food = customFoodRepository.getById(parameters.foodId) ?: return Result.Error("Продукт не найден")
        if (parameters.grams <= 0) return Result.Error("Укажите вес порции")

        val factor = parameters.grams / 100.0
        val dayId = dayProvider.currentDayId()
        val tracked = TrackedFood(
            id = 0,
            foodId = food.id,
            name = food.name,
            grams = parameters.grams,
            calories = food.calories * factor,
            proteins = food.proteins * factor,
            fats = food.fats * factor,
            carbs = food.carbs * factor,
            dayId = dayId,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        trackedFoodRepository.add(tracked)
        return Result.Success(dayId)
    }

    data class Parameters(val foodId: Long, val grams: Int)

    sealed class Result {
        data class Success(val dayId: String) : Result()
        data class Error(val message: String) : Result()
    }
}
