package com.kurban.calory.features.customfood.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.features.customfood.domain.model.CustomFood
import kotlinx.coroutines.CoroutineDispatcher

class CreateCustomFoodUseCase(
    private val repository: CustomFoodRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<CreateCustomFoodUseCase.Parameters, CustomFood>(dispatcher) {

    override suspend fun execute(parameters: Parameters): CustomFood {
        val name = parameters.name.trim()
        if (name.isEmpty()) {
            throw DomainError.ValidationError(originalMessage = "Введите название продукта")
        }

        val values = listOf(parameters.calories, parameters.proteins, parameters.fats, parameters.carbs)
        if (values.any { it.isNaN() }) {
            throw DomainError.ValidationError(originalMessage = "Заполните поля цифрами")
        }
        if (parameters.calories <= 0) {
            throw DomainError.ValidationError(originalMessage = "Калории должны быть больше нуля")
        }
        if (parameters.proteins < 0 || parameters.fats < 0 || parameters.carbs < 0) {
            throw DomainError.ValidationError(originalMessage = "Питательные данные не могут быть отрицательными")
        }

        return repository.add(
            NewCustomFood(
                name = name,
                calories = parameters.calories,
                proteins = parameters.proteins,
                fats = parameters.fats,
                carbs = parameters.carbs
            )
        )
    }

    data class Parameters(
        val name: String,
        val calories: Double,
        val proteins: Double,
        val fats: Double,
        val carbs: Double
    )
}
