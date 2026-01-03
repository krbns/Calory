package com.kurban.calory.features.main.ui.model

data class UIDay(
    val id: String,
    val dayNumber: String,
    val weekLetter: String,
    val label: String,
    val isToday: Boolean,
    val isFuture: Boolean,
    val isSelected: Boolean
)
