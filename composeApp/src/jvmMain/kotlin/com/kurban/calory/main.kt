package com.kurban.calory

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.data.db.DriverContext
import com.kurban.calory.core.di.initKoin
import com.kurban.calory.core.navigation.DefaultRootComponent

fun main() = application {
    val lifecycle = LifecycleRegistry()

    initKoin(DatabaseDriverFactory(DriverContext()))

    val root = runOnUiThread {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }

//    application {
        val windowState = rememberWindowState(width = 420.dp, height = 760.dp)

        LifecycleController(lifecycle, windowState)
        Window(
            onCloseRequest = ::exitApplication,
            title = "Calory",
            state = windowState,
        ) {
            AppRoot(root)
        }
//    }
}
