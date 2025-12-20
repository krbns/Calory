package com.kurban.calory.features.customfood.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.customfood.domain.model.CustomFood
import kotlinx.coroutines.CoroutineDispatcher

class CreateCustomFoodUseCase(
    private val repository: CustomFoodRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<CreateCustomFoodUseCase.Parameters, CreateCustomFoodUseCase.Result>(dispatcher) {

    override suspend fun execute(parameters: Parameters): Result {
        val name = parameters.name.trim()
        if (name.isEmpty()) return Result.Error("Введите название продукта")

        val values = listOf(parameters.calories, parameters.proteins, parameters.fats, parameters.carbs)
        if (values.any { it.isNaN() }) return Result.Error("Заполните поля цифрами")
        if (parameters.calories <= 0) return Result.Error("Калории должны быть больше нуля")
        if (parameters.proteins < 0 || parameters.fats < 0 || parameters.carbs < 0) {
            return Result.Error("Питательные данные не могут быть отрицательными")
        }

        return try {
            val food = repository.add(
                NewCustomFood(
                    name = name,
                    calories = parameters.calories,
                    proteins = parameters.proteins,
                    fats = parameters.fats,
                    carbs = parameters.carbs
                )
            )
            Result.Success(food)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Не удалось сохранить продукт")
        }
    }

    data class Parameters(
        val name: String,
        val calories: Double,
        val proteins: Double,
        val fats: Double,
        val carbs: Double
    )

    sealed class Result {
        data class Success(val food: CustomFood) : Result()
        data class Error(val message: String) : Result()
    }
}
