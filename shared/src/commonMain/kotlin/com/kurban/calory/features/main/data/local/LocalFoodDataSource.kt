package com.kurban.calory.features.main.data.local

import com.kurban.calory.features.main.data.FoodDataSource
import com.kurban.calory.features.main.domain.model.Food
import sqldelight.dbscheme.FoodDatabase

class LocalFoodDataSource(private val database: FoodDatabase) : FoodDataSource {
    override fun findFood(name: String): Food? {
        val food = database.foodQueries.findByName(name).executeAsOneOrNull() ?: return null
        return Food(
            id = food.id,
            grams = 0,
            name = food.name,
            calories = food.calories,
            proteins = food.proteins,
            fats = food.fats,
            carbs = food.carbs,
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
