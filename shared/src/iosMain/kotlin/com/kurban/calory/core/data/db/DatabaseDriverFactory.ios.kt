package com.kurban.calory.core.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import sqldelight.dbscheme.FoodDatabase

actual class DriverContext

actual class DatabaseDriverFactory actual constructor(
    @Suppress("unused") driverContext: DriverContext,
) {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(FoodDatabase.Schema, "food.db")
    }
}
