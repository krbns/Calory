package com.kurban.calory.features.main.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.main.domain.model.Food
import kotlinx.coroutines.CoroutineDispatcher

class SearchFoodUseCase(
    private val repository: FoodRepository,
    dispatcher: CoroutineDispatcher,
) : CoroutineUseCase<SearchFoodUseCase.Parameters, List<Food>>(coroutineDispatcher = dispatcher) {

    override suspend fun execute(parameters: Parameters): List<Food> {
        return repository.search(parameters.query)
    }

    data class Parameters(val query: String)
}
