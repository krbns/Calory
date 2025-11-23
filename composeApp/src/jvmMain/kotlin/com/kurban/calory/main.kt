package com.kurban.calory

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kurban.calory.main.MainScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Calory",
    ) {
        MainScreen()
    }
}