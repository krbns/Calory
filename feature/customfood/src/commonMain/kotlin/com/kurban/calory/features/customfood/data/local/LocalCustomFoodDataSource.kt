package com.kurban.calory.features.customfood.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kurban.calory.features.customfood.data.CustomFoodDataSource
import com.kurban.calory.features.customfood.domain.NewCustomFood
import com.kurban.calory.features.customfood.domain.model.CustomFood
import custom.CustomFoodEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sqldelight.customFoodScheme.custom.CustomFoodDatabase

class LocalCustomFoodDataSource(
    private val database: CustomFoodDatabase
) : CustomFoodDataSource {

    override fun observeAll(dispatcher: CoroutineDispatcher): Flow<List<CustomFood>> {
        return database.customFoodQueries.selectAllCustomFoods()
            .asFlow()
            .mapToList(dispatcher)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insert(food: NewCustomFood): CustomFood {
        database.customFoodQueries.insertCustomFood(
            name = food.name,
            calories = food.calories,
            proteins = food.proteins,
            fats = food.fats,
            carbs = food.carbs
        )
        val entity = database.customFoodQueries.selectCustomFoodByName(food.name).executeAsOne()
        return entity.toDomain()
    }

    override suspend fun getById(id: Long): CustomFood? {
        return database.customFoodQueries.selectCustomFoodById(id).executeAsOneOrNull()?.toDomain()
    }
}

private fun CustomFoodEntity.toDomain(): CustomFood {
    return CustomFood(
        id = id,
        name = name,
        calories = calories,
        proteins = proteins,
        fats = fats,
        carbs = carbs
    )
}
