package com.kurban.calory.features.main.data.local

import com.kurban.calory.features.main.data.FoodDataSource
import com.kurban.calory.features.main.domain.model.Food
import sqldelight.dbscheme.FoodDatabase

class LocalFoodDataSource(private val database: FoodDatabase) : FoodDataSource {
    override fun findFood(name: String): Food? {
        val food = database.foodQueries.findByName(name).executeAsOneOrNull()
        return Food(
            id = food?.id ?: 0,
            grams = 0,
            name = food?.name.orEmpty(),
            calories = food?.calories ?: 0.0,
            proteins = food?.proteins ?: 0.0,
            fats = food?.fats ?: 0.0,
            carbs = food?.carbs ?: 0.0,
        )
    }

    override fun search(query: String): List<Food> {
        return database.foodQueries.search("%$query%").executeAsList().map { it ->
            Food(
                id = it.id,
                grams = 0,
                name = it.name,
                calories = it.calories,
                proteins = it.proteins,
                fats = it.fats,
                carbs = it.carbs,
            )
        }
    }

    override fun addUserFood(
        name: String,
        grams: Int
    ): Food? {
        val base = findFood(name) ?: return null

        val factor = grams / 100.0

        return Food(
            id = base.id,
            name = base.name,
            grams = grams,
            calories = base.calories * factor,
            proteins = base.proteins * factor,
            fats = base.fats * factor,
            carbs = base.carbs * factor
        )
    }
}