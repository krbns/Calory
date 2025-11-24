package com.kurban.calory.core.data.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import sqldelight.dbscheme.FoodDatabase
import app.cash.sqldelight.db.SqlDriver

actual typealias DriverContext = Context

actual class DatabaseDriverFactory actual constructor(
    private val driverContext: DriverContext,
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            FoodDatabase.Schema,
            driverContext,
            "food.db"
        )
    }
}
