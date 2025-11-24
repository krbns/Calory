package com.kurban.calory

import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.data.db.DriverContext
import com.kurban.calory.core.di.initKoin
import org.koin.core.Koin

fun initKoinIos(): Koin {
    return initKoin(DatabaseDriverFactory(DriverContext()).createDriver())
}
