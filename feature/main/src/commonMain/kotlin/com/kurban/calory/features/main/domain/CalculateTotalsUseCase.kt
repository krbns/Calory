package com.kurban.calory.features.main.domain

import com.kurban.calory.features.main.domain.model.MacroTotals
import com.kurban.calory.features.main.domain.model.TrackedFood
import kotlin.math.roundToInt

class CalculateTotalsUseCase {
    operator fun invoke(consumed: List<TrackedFood>): MacroTotals {
        val totals = consumed.fold(MacroTotals()) { acc, food ->
            MacroTotals(
                calories = acc.calories + food.calories,
                proteins = acc.proteins + food.proteins,
                fats = acc.fats + food.fats,
                carbs = acc.carbs + food.carbs
            )
        }

        return MacroTotals(
            calories = totals.calories.roundToInt().toDouble(),
            proteins = totals.proteins.roundToInt().toDouble(),
            fats = totals.fats.roundToInt().toDouble(),
            carbs = totals.carbs.roundToInt().toDouble()
        )
    }
}
