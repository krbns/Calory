package com.kurban.calory.core.di

import app.cash.sqldelight.db.SqlDriver
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.data.db.sqldelight.DatabaseInitializer
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sqldelight.barcodeProductScheme.barcode.BarcodeProductDatabase
import sqldelight.customFoodScheme.custom.CustomFoodDatabase
import sqldelight.foodScheme.food.FoodDatabase
import sqldelight.trackedFoodScheme.tracked.TrackedFoodDatabase
import sqldelight.userProfileScheme.profile.UserProfileDatabase

const val FOOD_DATABASE_DRIVER = "FoodDatabaseDriver"
const val TRACKED_FOOD_DATABASE_DRIVER = "TrackedFoodDatabaseDriver"
const val USER_PROFILE_DATABASE_DRIVER = "UserProfileDatabaseDriver"
const val CUSTOM_FOOD_DATABASE_DRIVER = "CustomFoodDatabaseDriver"
const val BARCODE_PRODUCT_DATABASE_DRIVER = "BarcodeProductDatabaseDriver"

fun coreDatabaseModule(driverFactory: DatabaseDriverFactory): Module = module {
    single { driverFactory }

    single<SqlDriver>(qualifier = named(FOOD_DATABASE_DRIVER)) { get<DatabaseDriverFactory>().createDriverForFoodDatabase() }
    single<SqlDriver>(qualifier = named(TRACKED_FOOD_DATABASE_DRIVER)) { get<DatabaseDriverFactory>().createDriverForTrackedDatabase() }
    single<SqlDriver>(qualifier = named(USER_PROFILE_DATABASE_DRIVER)) { get<DatabaseDriverFactory>().createDriverForUserProfileDatabase() }
    single<SqlDriver>(qualifier = named(CUSTOM_FOOD_DATABASE_DRIVER)) { get<DatabaseDriverFactory>().createDriverForCustomFoodDatabase() }
    single<SqlDriver>(qualifier = named(BARCODE_PRODUCT_DATABASE_DRIVER)) { get<DatabaseDriverFactory>().createDriverForBarcodeProductDatabase() }

    single<FoodDatabase> {
        val sqlDriver: SqlDriver = get(named(FOOD_DATABASE_DRIVER))
        DatabaseInitializer.initDatabase(sqlDriver)
        DatabaseInitializer.db
    }

    single<TrackedFoodDatabase> {
        val sqlDriver: SqlDriver = get(named(TRACKED_FOOD_DATABASE_DRIVER))
        TrackedFoodDatabase(sqlDriver)
    }

    single<UserProfileDatabase> {
        val sqlDriver: SqlDriver = get(named(USER_PROFILE_DATABASE_DRIVER))
        UserProfileDatabase(sqlDriver)
    }

    single<CustomFoodDatabase> {
        val sqlDriver: SqlDriver = get(named(CUSTOM_FOOD_DATABASE_DRIVER))
        CustomFoodDatabase(sqlDriver)
    }

    single<BarcodeProductDatabase> {
        val sqlDriver: SqlDriver = get(named(BARCODE_PRODUCT_DATABASE_DRIVER))
        BarcodeProductDatabase(sqlDriver)
    }
}
