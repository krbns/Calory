package com.kurban.calory.features.main.data

import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.main.domain.FoodRepository

class DefaultFoodRepository(private val dataSource: FoodDataSource): FoodRepository {
    override fun findFood(name: String): Food? {
        return dataSource.findFood(name)
    }

    override fun search(query: String): List<Food> {
        return dataSource.search(query)
    }

    override fun addUserFood(
        name: String,
        grams: Int
    ): Food? {
        return dataSource.addUserFood(name, grams)
    }
}