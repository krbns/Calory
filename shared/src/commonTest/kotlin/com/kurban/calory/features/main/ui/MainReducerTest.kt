package com.kurban.calory.features.main.ui

import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.main.ui.logic.mainReducer
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainUiState
import com.kurban.calory.features.main.ui.model.UITrackedFood
import com.kurban.calory.features.main.ui.model.UIDay
import com.kurban.calory.features.main.domain.model.MacroTotals
import com.kurban.calory.features.profile.domain.model.MacroTargets
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainReducerTest {

    private val reducer = mainReducer()

    @Test
    fun `query changed sets text and searching`() {
        val initial = MainUiState()

        val updated = reducer(initial, MainAction.QueryChanged("apple"))

        assertEquals("apple", updated.query)
        assertTrue(updated.isSearching)
    }

    @Test
    fun `grams changed filters only digits and separators`() {
        val initial = MainUiState()

        val updated = reducer(initial, MainAction.GramsChanged("12a3,4"))

        assertEquals("123,4", updated.gramsInput)
    }

    @Test
    fun `search success updates results and keeps selection`() {
        val foods = listOf(Food(1, 0, "Apple", 52.0, 0.3, 0.2, 14.0))
        val initial = MainUiState(selectedFood = null)

        val updated = reducer(initial, MainAction.SearchSuccess(foods))

        assertEquals(foods, updated.searchResults)
        assertEquals(foods.first(), updated.selectedFood)
        assertEquals(false, updated.isSearching)
    }

    @Test
    fun `load day success updates totals and tracked`() {
        val totals = MacroTotals(calories = 100.0, proteins = 10.0, fats = 5.0, carbs = 20.0)
        val items = listOf(UITrackedFood(1, 1, "Apple", 100, 52.0, 0.3, 0.2, 14.0))
        val initial = MainUiState()

        val updated = reducer(initial, MainAction.LoadDaySuccess(items, totals))

        assertEquals(items, updated.tracked)
        assertEquals(totals.calories, updated.totalCalories)
        assertEquals(totals.proteins, updated.totalProteins)
        assertEquals(totals.fats, updated.totalFats)
        assertEquals(totals.carbs, updated.totalCarbs)
    }

    @Test
    fun `load profile success stores targets`() {
        val targets = MacroTargets(calories = 2000.0, proteins = 150.0, fats = 70.0, carbs = 220.0)
        val initial = MainUiState(macroTargets = null)

        val updated = reducer(initial, MainAction.LoadProfileSuccess(targets))

        assertEquals(targets, updated.macroTargets)
    }

    @Test
    fun `load day action marks selection`() {
        val days = listOf(
            UIDay(id = "2024-01-01", dayNumber = "1", weekLetter = "П", label = "01.01", isToday = false, isSelected = false),
            UIDay(id = "2024-01-02", dayNumber = "2", weekLetter = "В", label = "02.01", isToday = true, isSelected = true)
        )
        val initial = MainUiState(
            days = days,
            selectedDayId = "2024-01-01",
            todayId = "2024-01-02"
        )

        val updated = reducer(initial, MainAction.LoadDay("2024-01-02"))

        assertEquals("2024-01-02", updated.selectedDayId)
        assertTrue(updated.days.first { it.id == "2024-01-02" }.isSelected)
        assertFalse(updated.days.first { it.id == "2024-01-01" }.isSelected)
    }
}
