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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainComponent(
    val componentContext: ComponentContext,
    val onOpenProfile: () -> Unit,
    val onOpenCustomFoods: () -> Unit,
) : ComponentContext by componentContext, KoinComponent {

    private val searchFood: SearchFoodUseCase by inject()
    private val observeTrackedForDayUseCase: ObserveTrackedForDayUseCase by inject()
    private val deleteTrackedFood: DeleteTrackedFoodUseCase by inject()
    private val addTrackedFoodUseCase: AddTrackedFoodUseCase by inject()
    private val calculateTotalsUseCase: CalculateTotalsUseCase by inject()
    private val calculateMacroTargetsUseCase: CalculateMacroTargetsUseCase by inject()
    private val observeUserProfileUseCase: ObserveUserProfileUseCase by inject()
    private val dispatchers: AppDispatchers by inject()
    private val dayProvider: DayProvider by inject()
    private val scope = componentScope()

    private val store = Store(
        initialState = MainUiState(),
        reducer = mainReducer(),
        middlewares = listOf(
            SearchMiddleware(searchFood, dispatchers, scope),
            ObserveDayMiddleware(observeTrackedForDayUseCase, calculateTotalsUseCase, dispatchers, scope),
            ObserveUserProfileMiddleware(observeUserProfileUseCase, calculateMacroTargetsUseCase, scope),
            AddFoodMiddleware(addTrackedFoodUseCase),
            RemoveEntryMiddleware(deleteTrackedFood, dayProvider)
        ),
        scope = scope,
        initialActions = listOf(
            MainAction.LoadDay(dayProvider.currentDayId()),
            MainAction.ObserveProfile
        )
    )

    val state: Value<MainUiState> = store.state
    val effects: SharedFlow<MainEffect> = store.effects

    fun dispatch(intent: MainIntent) {
        store.dispatch(
            when (intent) {
                MainIntent.LoadToday -> MainAction.LoadDay(dayProvider.currentDayId())
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
