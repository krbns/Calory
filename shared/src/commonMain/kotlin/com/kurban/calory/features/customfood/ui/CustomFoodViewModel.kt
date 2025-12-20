package com.kurban.calory.features.customfood.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurban.calory.core.ui.mvi.Store
import com.kurban.calory.features.customfood.domain.AddCustomFoodToDiaryUseCase
import com.kurban.calory.features.customfood.domain.CreateCustomFoodUseCase
import com.kurban.calory.features.customfood.domain.ObserveCustomFoodsUseCase
import com.kurban.calory.features.customfood.ui.logic.AddCustomFoodToDiaryMiddleware
import com.kurban.calory.features.customfood.ui.logic.CreateCustomFoodMiddleware
import com.kurban.calory.features.customfood.ui.logic.ObserveCustomFoodsMiddleware
import com.kurban.calory.features.customfood.ui.logic.customFoodReducer
import com.kurban.calory.features.customfood.ui.model.CustomFoodAction
import com.kurban.calory.features.customfood.ui.model.CustomFoodEffect
import com.kurban.calory.features.customfood.ui.model.CustomFoodIntent
import com.kurban.calory.features.customfood.ui.model.CustomFoodUiState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class CustomFoodViewModel(
    observeCustomFoodsUseCase: ObserveCustomFoodsUseCase,
    createCustomFoodUseCase: CreateCustomFoodUseCase,
    addCustomFoodToDiaryUseCase: AddCustomFoodToDiaryUseCase
) : ViewModel() {

    private val store = Store(
        initialState = CustomFoodUiState(),
        reducer = customFoodReducer(),
        middlewares = listOf(
            ObserveCustomFoodsMiddleware(observeCustomFoodsUseCase, viewModelScope),
            CreateCustomFoodMiddleware(createCustomFoodUseCase),
            AddCustomFoodToDiaryMiddleware(addCustomFoodToDiaryUseCase)
        ),
        scope = viewModelScope,
        initialActions = listOf(CustomFoodAction.ObserveFoods)
    )

    val state: StateFlow<CustomFoodUiState> = store.state
    val effects: SharedFlow<CustomFoodEffect> = store.effects

    fun dispatch(intent: CustomFoodIntent) {
        store.dispatch(
            when (intent) {
                is CustomFoodIntent.QueryChanged -> CustomFoodAction.QueryChanged(intent.query)
                is CustomFoodIntent.CreateFood -> CustomFoodAction.CreateFood(
                    CreateCustomFoodUseCase.Parameters(
                        name = intent.name,
                        calories = parseNumber(intent.calories),
                        proteins = parseNumber(intent.proteins),
                        fats = parseNumber(intent.fats),
                        carbs = parseNumber(intent.carbs)
                    )
                )

                is CustomFoodIntent.AddToDiary -> CustomFoodAction.AddToDiary(intent.foodId, intent.grams)
                CustomFoodIntent.ClearError -> CustomFoodAction.ClearError
                CustomFoodIntent.Load -> CustomFoodAction.ObserveFoods
            }
        )
    }

    private fun parseNumber(input: String): Double {
        val normalized = input.trim()
        if (normalized.isEmpty()) return 0.0
        return normalized.replace(',', '.').toDoubleOrNull() ?: Double.NaN
    }
}
