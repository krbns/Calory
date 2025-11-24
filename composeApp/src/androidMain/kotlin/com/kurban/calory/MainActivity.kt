package com.kurban.calory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.di.initKoin
import com.kurban.calory.features.main.ui.MainViewModel
import com.kurban.calory.features.main.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val koin = initKoin(DatabaseDriverFactory(this).createDriver())

        setContent {
            val viewModel = remember {
                koin.get<MainViewModel>()
            }
            MainScreen(viewModel = viewModel)
        }
    }
}
