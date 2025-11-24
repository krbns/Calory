package com.kurban.calory.core.di

import com.kurban.calory.core.data.db.sqldelight.DatabaseInitializer
import com.kurban.calory.features.main.data.DefaultFoodRepository
import com.kurban.calory.features.main.data.FoodDataSource
import com.kurban.calory.features.main.data.local.LocalFoodDataSource
import com.kurban.calory.features.main.domain.FoodRepository
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.ui.MainViewModel
import app.cash.sqldelight.db.SqlDriver
import org.koin.dsl.module
import sqldelight.dbscheme.FoodDatabase
import org.koin.core.Koin
import org.koin.core.context.startKoin

fun appModule(driver: SqlDriver) = module {

    single { driver }

    single<FoodDatabase> {
        val sqlDriver: SqlDriver = get()
        DatabaseInitializer.initDatabase(sqlDriver)
        DatabaseInitializer.db
    }
    single<FoodDataSource> { LocalFoodDataSource(get()) }
    single<FoodRepository> { DefaultFoodRepository(get()) }
    factory { SearchFoodUseCase(get()) }
    factory { MainViewModel(get(), get()) }

}
    // TODO убрать
private var koinInstance: Koin? = null

fun initKoin(driver: SqlDriver): Koin {
    val existing = koinInstance
    if (existing != null) return existing

    val created = startKoin {
        modules(appModule(driver))
    }.koin
    koinInstance = created
    return created
}
