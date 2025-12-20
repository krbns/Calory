package com.kurban.calory.features.customfood.domain

import com.kurban.calory.features.customfood.domain.model.CustomFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class ObserveCustomFoodsUseCase(
    private val repository: CustomFoodRepository,
    private val dispatcher: CoroutineDispatcher
) {

    operator fun invoke(): Flow<List<CustomFood>> {
        return repository.observeAll(dispatcher).flowOn(dispatcher)
    }
}
