package com.kurban.calory.core.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val DarkColors = darkColorScheme(
    primary = Color(0xFF8EF7B5),
    onPrimary = Color(0xFF013220),
    secondary = Color(0xFFFFCE89),
    onSecondary = Color(0xFF2D1600),
    tertiary = Color(0xFFB7C4FF),
    background = Color(0xFF0B132B),
    onBackground = Color(0xFFE9F1FF),
    surface = Color(0xFF111B34),
    onSurface = Color(0xFFE9F1FF),
    surfaceVariant = Color(0xFF1B2A4A),
    onSurfaceVariant = Color(0xFFC5D1EC),
    error = Color(0xFFFF6B6B)
)

internal val LightColors = lightColorScheme(
    primary = Color(0xFF0F9D58),
    onPrimary = Color.White,
    secondary = Color(0xFFFFB347),
    onSecondary = Color(0xFF241200),
    tertiary = Color(0xFF4E73FF),
    background = Color(0xFFF4F7FF),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE8EDFB),
    onSurfaceVariant = Color(0xFF4B5567),
    error = Color(0xFFB3261E)
)
