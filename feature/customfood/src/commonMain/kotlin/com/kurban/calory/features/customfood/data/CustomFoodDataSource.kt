package com.kurban.calory.features.customfood.data

import com.kurban.calory.features.customfood.domain.NewCustomFood
import com.kurban.calory.features.customfood.domain.model.CustomFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface CustomFoodDataSource {
    fun observeAll(dispatcher: CoroutineDispatcher): Flow<List<CustomFood>>
    suspend fun insert(food: NewCustomFood): CustomFood
    suspend fun getById(id: Long): CustomFood?
}
