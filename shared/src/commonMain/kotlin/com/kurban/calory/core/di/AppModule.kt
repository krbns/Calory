package com.kurban.calory.core.di

import app.cash.sqldelight.db.SqlDriver
import com.kurban.calory.core.data.db.sqldelight.Database
import com.kurban.calory.features.main.data.DefaultFoodRepository
import com.kurban.calory.features.main.data.FoodDataSource
import com.kurban.calory.features.main.data.local.LocalFoodDataSource
import com.kurban.calory.features.main.domain.FoodRepository
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import org.koin.dsl.module
import sqldelight.dbscheme.FoodDatabase

val appModule = module {

    single<FoodDatabase> {
        val driver: SqlDriver = get()
        Database.initDatabase(driver)
        Database.db
    }
    single<FoodDataSource> { LocalFoodDataSource(get()) }
    single<FoodRepository> { DefaultFoodRepository(get()) }
    factory { SearchFoodUseCase(get()) }

}
