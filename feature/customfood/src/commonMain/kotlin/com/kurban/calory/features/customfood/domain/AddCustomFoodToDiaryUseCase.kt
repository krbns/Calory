package com.kurban.calory.features.customfood.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.core.domain.DomainError
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
) : CoroutineUseCase<AddCustomFoodToDiaryUseCase.Parameters, String>(dispatcher) {

    override suspend fun execute(parameters: Parameters): String {
        val food = customFoodRepository.getById(parameters.foodId)
            ?: throw DomainError.ValidationError(originalMessage = "Продукт не найден")
        if (parameters.grams <= 0) {
            throw DomainError.ValidationError(originalMessage = "Укажите вес порции")
        }

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
        return dayId
    }

    data class Parameters(val foodId: Long, val grams: Int)
}
