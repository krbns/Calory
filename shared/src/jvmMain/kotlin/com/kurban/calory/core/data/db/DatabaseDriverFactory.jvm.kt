package com.kurban.calory.core.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import sqldelight.dbscheme.FoodDatabase

actual class DriverContext

actual class DatabaseDriverFactory actual constructor(
    @Suppress("unused") driverContext: DriverContext,
) {
    actual fun createDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:calory.db")
        runCatching { FoodDatabase.Schema.create(driver) }
        return driver
    }
}
