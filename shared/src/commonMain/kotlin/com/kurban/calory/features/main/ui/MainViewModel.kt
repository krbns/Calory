package com.kurban.calory.features.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.ui.mvi.Store
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
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
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(
    searchFood: SearchFoodUseCase,
    observeTrackedForDayUseCase: ObserveTrackedForDayUseCase,
    deleteTrackedFood: DeleteTrackedFoodUseCase,
    addTrackedFoodUseCase: AddTrackedFoodUseCase,
    calculateTotalsUseCase: CalculateTotalsUseCase,
    calculateMacroTargetsUseCase: CalculateMacroTargetsUseCase,
    observeUserProfileUseCase: ObserveUserProfileUseCase,
    dispatchers: AppDispatchers,
    private val dayProvider: DayProvider,
) : ViewModel() {

    private val store = Store(
        initialState = MainUiState(),
        reducer = mainReducer(),
        middlewares = listOf(
            SearchMiddleware(searchFood, dispatchers, viewModelScope),
            ObserveDayMiddleware(observeTrackedForDayUseCase, calculateTotalsUseCase, dispatchers, viewModelScope),
            ObserveUserProfileMiddleware(observeUserProfileUseCase, calculateMacroTargetsUseCase, viewModelScope),
            AddFoodMiddleware(addTrackedFoodUseCase),
            RemoveEntryMiddleware(deleteTrackedFood, dayProvider)
        ),
        scope = viewModelScope,
        initialActions = listOf(
            MainAction.LoadDay(dayProvider.currentDayId()),
            MainAction.ObserveProfile
        )
    )

    val uiState: StateFlow<MainUiState> = store.state
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
