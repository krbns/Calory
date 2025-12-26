package com.kurban.calory.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun CaloryTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = if (darkTheme) DarkColors else LightColors
    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalElevation provides Elevation()
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = MaterialTheme.typography,
            content = content
        )
    }

}
