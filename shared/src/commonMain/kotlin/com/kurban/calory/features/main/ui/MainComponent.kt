package com.kurban.calory.features.main.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.navigation.componentScope
import com.kurban.calory.core.ui.mvi.Store
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.DeleteTrackedFoodUseCase
import com.kurban.calory.features.main.domain.ObserveTrackedForDayUseCase
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.ui.logic.AddFoodMiddleware
import com.kurban.calory.features.main.ui.logic.ObserveDayMiddleware
import com.kurban.calory.features.main.ui.logic.RemoveEntryMiddleware
import com.kurban.calory.features.main.ui.logic.SearchMiddleware
import com.kurban.calory.features.main.ui.logic.mainReducer
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainIntent
import com.kurban.calory.features.main.ui.model.MainUiState
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import com.kurban.calory.features.profile.ui.logic.ObserveUserProfileMiddleware
import kotlinx.coroutines.flow.SharedFlow

class MainComponent(
    val componentContext: ComponentContext,
    private val dependencies: MainDependencies,
    val onOpenProfile: () -> Unit,
    val onOpenCustomFoods: () -> Unit,
) : ComponentContext by componentContext {
    private val scope = componentScope()

    private val store = Store(
        initialState = MainUiState(),
        reducer = mainReducer(),
        middlewares = listOf(
            SearchMiddleware(dependencies.searchFoodUseCase, dependencies.dispatchers, scope),
            ObserveDayMiddleware(
                dependencies.observeTrackedForDayUseCase,
                dependencies.calculateTotalsUseCase,
                dependencies.dispatchers,
                scope
            ),
            ObserveUserProfileMiddleware(
                dependencies.observeUserProfileUseCase,
                dependencies.calculateMacroTargetsUseCase,
                scope
            ),
            AddFoodMiddleware(dependencies.addTrackedFoodUseCase),
            RemoveEntryMiddleware(dependencies.deleteTrackedFoodUseCase, dependencies.dayProvider)
        ),
        scope = scope,
        initialActions = listOf(
            MainAction.LoadDay(dependencies.dayProvider.currentDayId()),
            MainAction.ObserveProfile
        )
    )

    val state: Value<MainUiState> = store.state
    val effects: SharedFlow<MainEffect> = store.effects

    fun dispatch(intent: MainIntent) {
        store.dispatch(
            when (intent) {
                MainIntent.LoadToday -> MainAction.LoadDay(dependencies.dayProvider.currentDayId())
                is MainIntent.QueryChanged -> MainAction.QueryChanged(intent.query)
                is MainIntent.FoodSelected -> MainAction.FoodSelected(intent.food)
                is MainIntent.GramsChanged -> MainAction.GramsChanged(intent.gramsInput)
                MainIntent.AddSelectedFood -> MainAction.AddSelectedFood
                is MainIntent.RemoveEntry -> MainAction.RemoveEntry(intent.entryId)
                MainIntent.ClearError -> MainAction.ClearError
            }
        )
    }
}

data class MainDependencies(
    val searchFoodUseCase: SearchFoodUseCase,
    val observeTrackedForDayUseCase: ObserveTrackedForDayUseCase,
    val deleteTrackedFoodUseCase: DeleteTrackedFoodUseCase,
    val addTrackedFoodUseCase: AddTrackedFoodUseCase,
    val calculateTotalsUseCase: CalculateTotalsUseCase,
    val calculateMacroTargetsUseCase: CalculateMacroTargetsUseCase,
    val observeUserProfileUseCase: ObserveUserProfileUseCase,
    val dispatchers: AppDispatchers,
    val dayProvider: DayProvider
)
