package com.kurban.calory.core.data.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import sqldelight.dbscheme.FoodDatabase

actual typealias DriverContext = Context

actual class DatabaseDriverFactory actual constructor(
    private val driverContext: DriverContext,
) {
    actual fun createDriverForFoodDatabase(): SqlDriver {
        return AndroidSqliteDriver(
            FoodDatabase.Schema,
            driverContext,
            "food.db"
        )
    }
}
