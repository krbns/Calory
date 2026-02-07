package com.kurban.calory.features.main.di

import com.kurban.calory.core.domain.AppDispatchers
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
import org.koin.dsl.module
import sqldelight.foodScheme.food.FoodDatabase
import sqldelight.trackedFoodScheme.tracked.TrackedFoodDatabase

val featureMainModule = module {
    single<FoodDataSource> { LocalFoodDataSource(get<FoodDatabase>()) }
    single<FoodRepository> { DefaultFoodRepository(get()) }
    single<TrackedFoodDataSource> { LocalTrackedFoodDataSource(get<TrackedFoodDatabase>()) }
    single<TrackedFoodRepository> { DefaultTrackedFoodRepository(get()) }

    factory { SearchFoodUseCase(get(), get<AppDispatchers>().io) }
    factory { AddTrackedFoodUseCase(get(), get(), get(), get<AppDispatchers>().io) }
    factory { ObserveTrackedForDayUseCase(get(), get<AppDispatchers>().io) }
    factory { DeleteTrackedFoodUseCase(get(), get<AppDispatchers>().io) }
    factory { CalculateTotalsUseCase() }
}
