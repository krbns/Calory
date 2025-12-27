package com.kurban.calory.features.customfood.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.kurban.calory.core.navigation.componentScope
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

class CustomFoodComponent(
    val componentContext: ComponentContext,
    dependencies: CustomFoodDependencies,
    val onBack: () -> Unit,
) : ComponentContext by componentContext {
    private val scope = componentScope()

    private val store = Store(
        initialState = CustomFoodUiState(),
        reducer = customFoodReducer(),
        middlewares = listOf(
            ObserveCustomFoodsMiddleware(dependencies.observeCustomFoodsUseCase, scope),
            CreateCustomFoodMiddleware(dependencies.createCustomFoodUseCase),
            AddCustomFoodToDiaryMiddleware(dependencies.addCustomFoodToDiaryUseCase)
        ),
        scope = scope,
        initialActions = listOf(CustomFoodAction.ObserveFoods)
    )

    val state: Value<CustomFoodUiState> = store.state
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

data class CustomFoodDependencies(
    val observeCustomFoodsUseCase: ObserveCustomFoodsUseCase,
    val createCustomFoodUseCase: CreateCustomFoodUseCase,
    val addCustomFoodToDiaryUseCase: AddCustomFoodToDiaryUseCase
)
