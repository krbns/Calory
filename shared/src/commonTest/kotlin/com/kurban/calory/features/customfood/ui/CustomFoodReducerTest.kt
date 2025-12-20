package com.kurban.calory.features.customfood.ui

import com.kurban.calory.features.customfood.domain.model.CustomFood
import com.kurban.calory.features.customfood.ui.logic.customFoodReducer
import com.kurban.calory.features.customfood.ui.model.CustomFoodAction
import com.kurban.calory.features.customfood.ui.model.CustomFoodUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CustomFoodReducerTest {

    private val reducer = customFoodReducer()

    @Test
    fun `filters foods by query`() {
        val foods = listOf(
            CustomFood(1, "Яблоко", 50.0, 0.3, 0.2, 12.0),
            CustomFood(2, "Курица", 150.0, 20.0, 5.0, 0.0)
        )
        val state = CustomFoodUiState(foods = foods, filteredFoods = foods)

        val newState = reducer(state, CustomFoodAction.QueryChanged("яб"))

        assertEquals("яб", newState.query)
        assertEquals(1, newState.filteredFoods.size)
        assertTrue(newState.filteredFoods.first().name.contains("Яблоко"))
    }

    @Test
    fun `create action toggles saving flag`() {
        val state = CustomFoodUiState()
        val pending = reducer(state, CustomFoodAction.CreateFood(mockParameters()))
        val finished = reducer(pending, CustomFoodAction.CreateFoodSuccess)

        assertTrue(pending.isSaving)
        assertEquals(false, finished.isSaving)
    }

    private fun mockParameters() = com.kurban.calory.features.customfood.domain.CreateCustomFoodUseCase.Parameters(
        name = "Test",
        calories = 10.0,
        proteins = 1.0,
        fats = 1.0,
        carbs = 1.0
    )
}
