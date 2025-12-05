package com.kurban.calory

import android.app.Application
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.di.initKoin

class CaloryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(DatabaseDriverFactory(this@CaloryApp))
    }
}
