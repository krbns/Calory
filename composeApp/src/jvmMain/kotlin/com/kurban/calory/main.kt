package com.kurban.calory

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.data.db.DriverContext
import com.kurban.calory.core.di.initKoin

fun main() = application {
    initKoin(DatabaseDriverFactory(DriverContext()))
    Window(
        onCloseRequest = ::exitApplication,
        title = "Calory",
        state = rememberWindowState(width = 420.dp, height = 760.dp)
    ) {
        AppRoot()
    }
}
