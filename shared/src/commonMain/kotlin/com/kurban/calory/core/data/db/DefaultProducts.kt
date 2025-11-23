package com.kurban.calory.core.data.db

import sqldelight.dbscheme.FoodDatabase

object DefaultProducts {

    private val foods = listOf(
        FoodEntity("Банан", 89.0, 1.1, 0.3, 23.0),
        FoodEntity("Яблоко", 52.0, 0.3, 0.2, 14.0),
        FoodEntity("Груша", 57.0, 0.4, 0.1, 15.0),

        FoodEntity("Куриная грудка", 165.0, 31.0, 3.6, 0.0),
        FoodEntity("Говядина", 250.0, 26.0, 15.0, 0.0),
        FoodEntity("Свинина", 242.0, 27.0, 14.0, 0.0),

        FoodEntity("Рис", 130.0, 2.7, 0.3, 28.0),
        FoodEntity("Гречка", 343.0, 13.2, 3.4, 72.0),
        FoodEntity("Овсянка", 379.0, 17.0, 7.0, 67.0),

        FoodEntity("Яйцо", 155.0, 13.0, 11.0, 1.1),
        FoodEntity("Молоко", 64.0, 3.2, 3.5, 4.7),
        FoodEntity("Сыр", 402.0, 25.0, 33.0, 1.3),

        FoodEntity("Хлеб белый", 266.0, 8.0, 3.3, 49.0),
        FoodEntity("Хлеб ржаной", 259.0, 9.0, 4.2, 48.0)
    )

    fun load(db: FoodDatabase) {
        db.foodQueries.transaction {
            foods.forEach {
                db.foodQueries.insertFood(
                    name = it.name,
                    calories = it.calories,
                    proteins = it.proteins,
                    fats = it.fats,
                    carbs = it.carbs
                )
            }
        }
    }
}

data class FoodEntity(
    val name: String,
    val calories: Double,
    val proteins: Double,
    val fats: Double,
    val carbs: Double
)
