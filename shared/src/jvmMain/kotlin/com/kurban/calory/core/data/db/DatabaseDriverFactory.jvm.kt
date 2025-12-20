package com.kurban.calory.core.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import sqldelight.foodScheme.food.FoodDatabase
import sqldelight.trackedFoodScheme.tracked.TrackedFoodDatabase
import sqldelight.userProfileScheme.profile.UserProfileDatabase
import sqldelight.customFoodScheme.custom.CustomFoodDatabase

actual class DriverContext

actual class DatabaseDriverFactory actual constructor(
    @Suppress("unused") driverContext: DriverContext,
) {
    actual fun createDriverForFoodDatabase(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:food.db")
        runCatching { FoodDatabase.Schema.create(driver) }
        return driver
    }

    actual fun createDriverForTrackedDatabase(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:tracked.db")
        runCatching { TrackedFoodDatabase.Schema.create(driver) }
        return driver
    }

    actual fun createDriverForUserProfileDatabase(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:userProfile.db")
        runCatching { UserProfileDatabase.Schema.create(driver) }
        return driver
    }

    actual fun createDriverForCustomFoodDatabase(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:customFood.db")
        runCatching { CustomFoodDatabase.Schema.create(driver) }
        return driver
    }
}
