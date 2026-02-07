package com.kurban.calory.features.customfood.data

import com.kurban.calory.features.customfood.domain.CustomFoodRepository
import com.kurban.calory.features.customfood.domain.NewCustomFood
import com.kurban.calory.features.customfood.domain.model.CustomFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class DefaultCustomFoodRepository(
    private val dataSource: CustomFoodDataSource
) : CustomFoodRepository {

    override fun observeAll(dispatcher: CoroutineDispatcher): Flow<List<CustomFood>> = dataSource.observeAll(dispatcher)

    override suspend fun add(food: NewCustomFood): CustomFood = dataSource.insert(food)

    override suspend fun getById(id: Long): CustomFood? = dataSource.getById(id)
}
