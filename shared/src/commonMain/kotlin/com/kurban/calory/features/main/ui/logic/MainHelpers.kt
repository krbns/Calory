package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.features.main.domain.model.TrackedFood
import com.kurban.calory.features.main.ui.model.MacroTotals
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

fun calculateTotals(consumed: List<UITrackedFood>): MacroTotals {
    return consumed.fold(MacroTotals()) { acc, food ->
        MacroTotals(
            calories = acc.calories + food.calories,
            proteins = acc.proteins + food.proteins,
            fats = acc.fats + food.fats,
            carbs = acc.carbs + food.carbs
        )
    }
}
