package com.kurban.calory.core.data.db.sqldelight

import app.cash.sqldelight.db.SqlDriver
import com.kurban.calory.core.data.db.DefaultProducts
import sqldelight.dbscheme.FoodDatabase

object DatabaseInitializer {
    lateinit var db: FoodDatabase

    fun initDatabase(driver: SqlDriver) {
        db = FoodDatabase(driver)

        val count = db.foodQueries.count().executeAsOne()
        if (count == 0L) {
            DefaultProducts.load(db)
        }
    }
}
