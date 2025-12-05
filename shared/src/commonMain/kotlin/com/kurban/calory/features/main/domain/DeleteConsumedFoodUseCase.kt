package com.kurban.calory.features.main.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import kotlinx.coroutines.CoroutineDispatcher

class DeleteConsumedFoodUseCase(
    private val repository: TrackedFoodRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<DeleteConsumedFoodUseCase.Parameters, Unit>(dispatcher) {

    override suspend fun execute(parameters: Parameters) {
        repository.delete(parameters.id)
    }

    data class Parameters(val id: Long)
}
