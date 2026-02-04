package com.kurban.calory.core.di

import app.cash.sqldelight.db.SqlDriver
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.data.db.sqldelight.DatabaseInitializer
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.core.ui.time.DefaultDayProvider
import com.kurban.calory.features.barcode.data.DefaultBarcodeProductRepository
import com.kurban.calory.features.barcode.data.local.LocalBarcodeProductDataSource
import com.kurban.calory.features.barcode.data.remote.RemoteBarcodeProductDataSource
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import com.kurban.calory.features.barcode.domain.datasource.BarcodeProductDataSource
import com.kurban.calory.features.barcode.domain.scanner.BarcodeScanner
import com.kurban.calory.features.barcode.domain.usecase.AddScannedFoodToDiaryUseCase
import com.kurban.calory.features.barcode.domain.usecase.CleanExpiredCacheUseCase
import com.kurban.calory.features.barcode.domain.usecase.GetFavoriteProductsUseCase
import com.kurban.calory.features.barcode.domain.usecase.ScanBarcodeUseCase
import com.kurban.calory.features.barcode.domain.usecase.SearchProductByBarcodeUseCase
import com.kurban.calory.features.barcode.domain.usecase.ToggleFavoriteUseCase
import com.kurban.calory.features.customfood.data.CustomFoodDataSource
import com.kurban.calory.features.customfood.data.DefaultCustomFoodRepository
import com.kurban.calory.features.customfood.data.local.LocalCustomFoodDataSource
import com.kurban.calory.features.customfood.domain.AddCustomFoodToDiaryUseCase
import com.kurban.calory.features.customfood.domain.CreateCustomFoodUseCase
import com.kurban.calory.features.customfood.domain.CustomFoodRepository
import com.kurban.calory.features.customfood.domain.ObserveCustomFoodsUseCase
import com.kurban.calory.features.main.data.DefaultFoodRepository
import com.kurban.calory.features.main.data.DefaultTrackedFoodRepository
import com.kurban.calory.features.main.data.FoodDataSource
import com.kurban.calory.features.main.data.TrackedFoodDataSource
import com.kurban.calory.features.main.data.local.LocalFoodDataSource
import com.kurban.calory.features.main.data.local.LocalTrackedFoodDataSource
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.DeleteTrackedFoodUseCase
import com.kurban.calory.features.main.domain.FoodRepository
import com.kurban.calory.features.main.domain.ObserveTrackedForDayUseCase
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.domain.TrackedFoodRepository
import com.kurban.calory.features.profile.data.DefaultUserProfileRepository
import com.kurban.calory.features.profile.data.UserProfileDataSource
import com.kurban.calory.features.profile.data.local.LocalUserProfileDataSource
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.GetUserProfileUseCase
import com.kurban.calory.features.profile.domain.NeedsOnboardingUseCase
import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sqldelight.barcodeProductScheme.barcode.BarcodeProductDatabase
import sqldelight.customFoodScheme.custom.CustomFoodDatabase
import sqldelight.foodScheme.food.FoodDatabase
import sqldelight.trackedFoodScheme.tracked.TrackedFoodDatabase
import sqldelight.userProfileScheme.profile.UserProfileDatabase

private const val FOOD_DATABASE_DRIVER = "FoodDatabaseDriver"
private const val TRACKED_FOOD_DATABASE_DRIVER = "TrackedFoodDatabaseDriver"
private const val USER_PROFILE_DATABASE_DRIVER = "UserProfileDatabaseDriver"
private const val CUSTOM_FOOD_DATABASE_DRIVER = "CustomFoodDatabaseDriver"
private const val BARCODE_PRODUCT_DATABASE_DRIVER = "BarcodeProductDatabaseDriver"

val coreModule = module {
    single { AppDispatchers(io = Dispatchers.IO, main = Dispatchers.Main, default = Dispatchers.Default) }
    single<DayProvider> { DefaultDayProvider() }
}

private fun databaseModule(driverFactory: DatabaseDriverFactory) = module {
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

private val dataModule = module {
    single<FoodDataSource> { LocalFoodDataSource(get<FoodDatabase>()) }
    single<FoodRepository> { DefaultFoodRepository(get()) }
    single<TrackedFoodDataSource> { LocalTrackedFoodDataSource(get<TrackedFoodDatabase>()) }
    single<TrackedFoodRepository> { DefaultTrackedFoodRepository(get()) }
    single<UserProfileDataSource> { LocalUserProfileDataSource(get<UserProfileDatabase>(), get(named(USER_PROFILE_DATABASE_DRIVER))) }
    single<UserProfileRepository> { DefaultUserProfileRepository(get()) }
    single<CustomFoodDataSource> { LocalCustomFoodDataSource(get<CustomFoodDatabase>()) }
    single<CustomFoodRepository> { DefaultCustomFoodRepository(get()) }
    single { BarcodeScanner() }
    single<BarcodeProductDataSource>(named("local")) { LocalBarcodeProductDataSource(get<BarcodeProductDatabase>()) }
    single<BarcodeProductDataSource>(named("remote")) { RemoteBarcodeProductDataSource() }
    single<BarcodeProductRepository> { DefaultBarcodeProductRepository(get(named("local")), get(named("remote")), get()) }
}

val domainModule = module {
    factory { SearchFoodUseCase(get(), get<AppDispatchers>().io) }
    factory { AddTrackedFoodUseCase(get(), get(), get(), get<AppDispatchers>().io) }
    factory { ObserveTrackedForDayUseCase(get(), get<AppDispatchers>().io) }
    factory { DeleteTrackedFoodUseCase(get(), get<AppDispatchers>().io) }
    factory { CalculateTotalsUseCase() }
    factory { CalculateMacroTargetsUseCase() }
    factory { GetUserProfileUseCase(get(), get<AppDispatchers>().io) }
    factory { SaveUserProfileUseCase(get(), get<AppDispatchers>().io) }
    factory { ObserveUserProfileUseCase(get(), get<AppDispatchers>().io) }
    factory { NeedsOnboardingUseCase(get(), get<AppDispatchers>().io) }
    factory { ObserveCustomFoodsUseCase(get(), get<AppDispatchers>().io) }
    factory { CreateCustomFoodUseCase(get(), get<AppDispatchers>().io) }
    factory { AddCustomFoodToDiaryUseCase(get(), get(), get(), get<AppDispatchers>().io) }
    // Barcode scanning use cases
    factory { SearchProductByBarcodeUseCase(get(), get<AppDispatchers>().io) }
    factory { ScanBarcodeUseCase(get(), get<AppDispatchers>().io) }
    factory { AddScannedFoodToDiaryUseCase(get(), get(), get(), get<AppDispatchers>().io) }
    factory { ToggleFavoriteUseCase(get(), get<AppDispatchers>().io) }
    factory { CleanExpiredCacheUseCase(get(), get<AppDispatchers>().io) }
    factory { GetFavoriteProductsUseCase(get(), get<AppDispatchers>().io) }
}

fun appModules(driverFactory: DatabaseDriverFactory): List<Module> = listOf(
    coreModule,
    databaseModule(driverFactory),
    dataModule,
    domainModule
)
