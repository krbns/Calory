package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.features.main.domain.model.TrackedFood
import com.kurban.calory.features.main.ui.model.UITrackedFood

fun TrackedFood.toUi(): UITrackedFood = UITrackedFood(
    entryId = id,
    foodId = foodId,
    name = name,
    grams = grams,
    calories = calories,
    proteins = proteins,
    fats = fats,
    carbs = carbs
)
