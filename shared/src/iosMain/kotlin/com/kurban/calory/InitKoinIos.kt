package com.kurban.calory

import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.data.db.DriverContext
import com.kurban.calory.core.di.initKoin
import org.koin.core.Koin
import org.koin.core.KoinApplication

fun initKoinIos(): KoinApplication {
    return initKoin(DatabaseDriverFactory(DriverContext())) {}
}
