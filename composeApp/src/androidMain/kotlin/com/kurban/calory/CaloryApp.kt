package com.kurban.calory

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CaloryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CaloryApp)
            modules(
                appModule,
                module {
                    single { DatabaseDriverFactory(androidContext()) }
                    single<SqlDriver> { get<DatabaseDriverFactory>().createDriver() }
                }
            )
        }
    }
}