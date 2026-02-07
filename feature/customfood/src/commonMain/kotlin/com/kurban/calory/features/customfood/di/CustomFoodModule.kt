package com.kurban.calory.features.customfood.di

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.features.customfood.data.CustomFoodDataSource
import com.kurban.calory.features.customfood.data.DefaultCustomFoodRepository
import com.kurban.calory.features.customfood.data.local.LocalCustomFoodDataSource
import com.kurban.calory.features.customfood.domain.AddCustomFoodToDiaryUseCase
import com.kurban.calory.features.customfood.domain.CreateCustomFoodUseCase
import com.kurban.calory.features.customfood.domain.CustomFoodRepository
import com.kurban.calory.features.customfood.domain.ObserveCustomFoodsUseCase
import org.koin.dsl.module
import sqldelight.customFoodScheme.custom.CustomFoodDatabase

val featureCustomFoodModule = module {
    single<CustomFoodDataSource> { LocalCustomFoodDataSource(get<CustomFoodDatabase>()) }
    single<CustomFoodRepository> { DefaultCustomFoodRepository(get()) }

    factory { ObserveCustomFoodsUseCase(get(), get<AppDispatchers>().io) }
    factory { CreateCustomFoodUseCase(get(), get<AppDispatchers>().io) }
    factory { AddCustomFoodToDiaryUseCase(get(), get(), get(), get<AppDispatchers>().io) }
}
