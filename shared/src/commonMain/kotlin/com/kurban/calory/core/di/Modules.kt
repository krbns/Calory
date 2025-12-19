package com.kurban.calory.core.di

import app.cash.sqldelight.db.SqlDriver
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.data.db.sqldelight.DatabaseInitializer
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.features.main.data.DefaultFoodRepository
import com.kurban.calory.features.main.data.DefaultTrackedFoodRepository
import com.kurban.calory.features.main.data.FoodDataSource
import com.kurban.calory.features.main.data.local.LocalFoodDataSource
import com.kurban.calory.features.main.data.local.LocalTrackedFoodDataSource
import com.kurban.calory.features.main.data.TrackedFoodDataSource
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
import com.kurban.calory.features.main.domain.DeleteTrackedFoodUseCase
import com.kurban.calory.features.main.domain.FoodRepository
import com.kurban.calory.features.main.domain.GetTrackedForDayUseCase
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.domain.TrackedFoodRepository
import com.kurban.calory.features.main.ui.MainViewModel
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.core.ui.time.DefaultDayProvider
import com.kurban.calory.features.profile.data.DefaultUserProfileRepository
import com.kurban.calory.features.profile.data.UserProfileDataSource
import com.kurban.calory.features.profile.data.local.LocalUserProfileDataSource
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.GetUserProfileUseCase
import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.UserProfileRepository
import com.kurban.calory.features.profile.ui.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sqldelight.foodScheme.food.FoodDatabase
import sqldelight.trackedFoodScheme.tracked.TrackedFoodDatabase
import sqldelight.userProfileScheme.profile.UserProfileDatabase

private const val FOOD_DATABASE_DRIVER = "FoodDatabaseDriver"
private const val TRACKED_FOOD_DATABASE_DRIVER = "TrackedFoodDatabaseDriver"
private const val USER_PROFILE_DATABASE_DRIVER = "UserProfileDatabaseDriver"
fun dataModule(driverFactory: DatabaseDriverFactory) = module {
    single { driverFactory }

    single<SqlDriver>(qualifier = named(FOOD_DATABASE_DRIVER)) { get<DatabaseDriverFactory>().createDriverForFoodDatabase() }
    single<SqlDriver>(qualifier = named(TRACKED_FOOD_DATABASE_DRIVER)) { get<DatabaseDriverFactory>().createDriverForTrackedDatabase() }
    single<SqlDriver>(qualifier = named(USER_PROFILE_DATABASE_DRIVER)) { get<DatabaseDriverFactory>().createDriverForUserProfileDatabase() }

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

    single<FoodDataSource> { LocalFoodDataSource(get<FoodDatabase>()) }
    single<FoodRepository> { DefaultFoodRepository(get()) }
    single<TrackedFoodDataSource> { LocalTrackedFoodDataSource(get<TrackedFoodDatabase>()) }
    single<TrackedFoodRepository> { DefaultTrackedFoodRepository(get()) }
    single<UserProfileDataSource> { LocalUserProfileDataSource(get<UserProfileDatabase>()) }
    single<UserProfileRepository> { DefaultUserProfileRepository(get()) }
}

val domainModule = module {

    single { AppDispatchers(io = Dispatchers.IO, main = Dispatchers.Main, default = Dispatchers.Default) }
    single<DayProvider> { DefaultDayProvider() }

    factory { SearchFoodUseCase(get(), get<AppDispatchers>().io) }
    factory { AddTrackedFoodUseCase(get(), get(), get(), get<AppDispatchers>().io) }
    factory { GetTrackedForDayUseCase(get(), get<AppDispatchers>().io) }
    factory { DeleteTrackedFoodUseCase(get(), get<AppDispatchers>().io) }
    factory { CalculateTotalsUseCase() }
    factory { CalculateMacroTargetsUseCase() }
    factory { GetUserProfileUseCase(get(), get<AppDispatchers>().io) }
    factory { SaveUserProfileUseCase(get(), get<AppDispatchers>().io) }
    factory { ObserveUserProfileUseCase(get(), get<AppDispatchers>().io) }
}

val uiModule = module {
    factory { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { ProfileViewModel(get(), get(), get()) }
}
