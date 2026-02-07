package com.kurban.calory.features.main.domain

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.main.domain.model.TrackedFood
import kotlinx.coroutines.CoroutineDispatcher

class GetTrackedForDayUseCase(
    private val repository: TrackedFoodRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<GetTrackedForDayUseCase.Parameters, List<TrackedFood>>(dispatcher) {

    override suspend fun execute(parameters: Parameters): List<TrackedFood> {
        return repository.getByDay(parameters.dayId)
    }

    data class Parameters(val dayId: String)
}
