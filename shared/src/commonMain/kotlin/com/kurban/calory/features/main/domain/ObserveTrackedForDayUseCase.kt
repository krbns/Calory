package com.kurban.calory.features.main.domain

import com.kurban.calory.features.main.domain.model.TrackedFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class ObserveTrackedForDayUseCase(
    private val repository: TrackedFoodRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(dayId: String): Flow<List<TrackedFood>> {
        return repository.observeByDay(dayId, dispatcher).flowOn(dispatcher)
    }
}
