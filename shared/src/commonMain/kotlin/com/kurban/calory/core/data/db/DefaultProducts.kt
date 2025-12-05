package com.kurban.calory.core.data.db

import sqldelight.foodScheme.food.FoodDatabase


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
        FoodEntity("Творог 5%", 121.0, 17.0, 5.0, 3.0),
        FoodEntity("Йогурт без сахара", 59.0, 10.0, 0.4, 3.6),

        FoodEntity("Тунец консервированный", 132.0, 28.0, 1.0, 0.0),
        FoodEntity("Лосось", 208.0, 20.0, 13.0, 0.0),

        FoodEntity("Картофель", 77.0, 2.0, 0.1, 17.0),
        FoodEntity("Морковь", 41.0, 0.9, 0.2, 10.0),
        FoodEntity("Брокколи", 34.0, 2.8, 0.4, 7.0),
        FoodEntity("Шпинат", 23.0, 2.9, 0.4, 3.6),
        FoodEntity("Огурец", 15.0, 0.7, 0.1, 3.6),
        FoodEntity("Помидор", 18.0, 0.9, 0.2, 3.9),
        FoodEntity("Авокадо", 160.0, 2.0, 15.0, 9.0),

        FoodEntity("Чечевица вареная", 116.0, 9.0, 0.4, 20.0),
        FoodEntity("Нут вареный", 164.0, 9.0, 2.6, 27.0),
        FoodEntity("Фасоль красная", 127.0, 8.7, 0.5, 22.8),

        FoodEntity("Миндаль", 579.0, 21.0, 50.0, 22.0),
        FoodEntity("Арахис", 567.0, 25.0, 49.0, 16.0),
        FoodEntity("Грецкий орех", 654.0, 15.0, 65.0, 14.0),
        FoodEntity("Кешью", 553.0, 18.0, 44.0, 30.0),

        FoodEntity("Черника", 57.0, 0.7, 0.3, 14.0),
        FoodEntity("Клубника", 33.0, 0.7, 0.3, 8.0),
        FoodEntity("Апельсин", 47.0, 0.9, 0.1, 12.0),
        FoodEntity("Киви", 61.0, 1.1, 0.5, 15.0),

        FoodEntity("Макароны из твердых сортов", 131.0, 5.0, 1.1, 25.0),
        FoodEntity("Киноа", 368.0, 14.0, 6.1, 64.0),
        FoodEntity("Булгур", 342.0, 12.3, 1.3, 75.9),

        FoodEntity("Оливковое масло", 884.0, 0.0, 100.0, 0.0),
        FoodEntity("Подсолнечное масло", 884.0, 0.0, 100.0, 0.0),

        FoodEntity("Темный шоколад 70%", 598.0, 7.8, 42.6, 45.9),
        FoodEntity("Мед", 304.0, 0.3, 0.0, 82.0),

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
