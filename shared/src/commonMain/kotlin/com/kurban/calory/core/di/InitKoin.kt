package com.kurban.calory.core.di

import com.kurban.calory.core.data.db.DatabaseDriverFactory
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    driverFactory: DatabaseDriverFactory,
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication {

    return startKoin {
        appDeclaration()
        modules(
            dataModule(driverFactory),
            domainModule
        )
    }
}
