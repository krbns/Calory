package com.kurban.calory.core.data.db

import app.cash.sqldelight.db.SqlDriver

expect class DriverContext

expect class DatabaseDriverFactory(driverContext: DriverContext) {
    fun createDriver(): SqlDriver
}
