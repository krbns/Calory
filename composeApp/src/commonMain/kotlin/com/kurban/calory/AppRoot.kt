package com.kurban.calory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.kurban.calory.features.main.MainScreen
import com.kurban.calory.features.profile.ProfileScreen

@Composable
fun AppRoot() {
    var current by remember { mutableStateOf(Screen.Main) }

    when (current) {
        Screen.Main -> MainScreen(onOpenProfile = { current = Screen.Profile })
        Screen.Profile -> ProfileScreen(onBack = { current = Screen.Main })
    }
}

private enum class Screen {
    Main,
    Profile
}
