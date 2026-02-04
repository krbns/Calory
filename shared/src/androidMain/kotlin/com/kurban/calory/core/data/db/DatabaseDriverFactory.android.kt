package com.kurban.calory.core.data.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import sqldelight.foodScheme.food.FoodDatabase
import sqldelight.customFoodScheme.custom.CustomFoodDatabase
import sqldelight.trackedFoodScheme.tracked.TrackedFoodDatabase
import sqldelight.userProfileScheme.profile.UserProfileDatabase
import sqldelight.barcodeProductScheme.barcode.BarcodeProductDatabase

actual class DriverContext(
    val context: Context,
)

actual class DatabaseDriverFactory actual constructor(
    private val driverContext: DriverContext,
) {
    actual fun createDriverForFoodDatabase(): SqlDriver {
        return AndroidSqliteDriver(
            FoodDatabase.Schema,
            driverContext.context,
            "food.db"
        )
    }

    actual fun createDriverForTrackedDatabase(): SqlDriver {
        return AndroidSqliteDriver(
            TrackedFoodDatabase.Schema,
            driverContext.context,
            "trackedFood.db"
        )
    }

    actual fun createDriverForUserProfileDatabase(): SqlDriver {
        return AndroidSqliteDriver(
            UserProfileDatabase.Schema,
            driverContext.context,
            "userProfile.db"
        )
    }

    actual fun createDriverForCustomFoodDatabase(): SqlDriver {
        return AndroidSqliteDriver(
            CustomFoodDatabase.Schema,
            driverContext.context,
            "customFood.db"
        )
    }

    actual fun createDriverForBarcodeProductDatabase(): SqlDriver {
        return AndroidSqliteDriver(
            BarcodeProductDatabase.Schema,
            driverContext.context,
            "barcodeProduct.db"
        )
    }
}
