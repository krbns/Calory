package com.kurban.calory

import android.app.Application
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CaloryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CaloryApp)
            modules(
                appModule(
                    DatabaseDriverFactory(this@CaloryApp).createDriver()
                ),
            )
        }
    }
}