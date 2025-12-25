package com.kurban.calory.features.main.ui

import androidx.lifecycle.viewModelScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.kurban.calory.core.navigation.componentScope
import com.kurban.calory.core.ui.mvi.Store
import com.kurban.calory.features.main.ui.logic.AddFoodMiddleware
import com.kurban.calory.features.main.ui.logic.ObserveDayMiddleware
import com.kurban.calory.features.main.ui.logic.RemoveEntryMiddleware
import com.kurban.calory.features.main.ui.logic.SearchMiddleware
import com.kurban.calory.features.main.ui.logic.mainReducer
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainUiState
import com.kurban.calory.features.profile.ui.logic.ObserveUserProfileMiddleware

class MainComponent(
    val componentContext: ComponentContext,
    val onOpenProfile: () -> Unit,
    val onOpenCustomFoods: () -> Unit,
) : ComponentContext by componentContext {
//    private val store = Store(
//        initialState = MainUiState(),
//        reducer = mainReducer(),
//        middlewares = listOf(
//            SearchMiddleware(searchFood, dispatchers, viewModelScope),
//            ObserveDayMiddleware(observeTrackedForDayUseCase, calculateTotalsUseCase, dispatchers, viewModelScope),
//            ObserveUserProfileMiddleware(observeUserProfileUseCase, calculateMacroTargetsUseCase, viewModelScope),
//            AddFoodMiddleware(addTrackedFoodUseCase),
//            RemoveEntryMiddleware(deleteTrackedFood, dayProvider)
//        ),
//        scope = componentScope(),
//        initialActions = listOf(
//            MainAction.LoadDay(dayProvider.currentDayId()),
//            MainAction.ObserveProfile
//        )
//    )
//
//    val state: Value<UsersState> = store.state
}