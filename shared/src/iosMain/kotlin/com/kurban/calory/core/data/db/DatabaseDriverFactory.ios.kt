package com.kurban.calory.core.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import sqldelight.dbscheme.food.FoodDatabase
import sqldelight.dbscheme.tracked.TrackedFoodDatabase

actual class DriverContext

actual class DatabaseDriverFactory actual constructor(
    @Suppress("unused") driverContext: DriverContext,
) {
    actual fun createDriverForFoodDatabase(): SqlDriver {
        return NativeSqliteDriver(FoodDatabase.Schema, "food.db")
    }

    actual fun createDriverForTrackedDatabase(): SqlDriver {
        return NativeSqliteDriver(TrackedFoodDatabase.Schema, "trackedFood.db")
    }
}
