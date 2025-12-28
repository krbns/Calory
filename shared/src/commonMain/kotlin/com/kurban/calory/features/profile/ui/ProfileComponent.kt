package com.kurban.calory.features.profile.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.navigation.componentScope
import com.kurban.calory.core.ui.mvi.Store
import com.kurban.calory.features.profile.domain.GetUserProfileUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.ui.logic.LoadProfileMiddleware
import com.kurban.calory.features.profile.ui.logic.SaveProfileMiddleware
import com.kurban.calory.features.profile.ui.logic.profileReducer
import com.kurban.calory.features.profile.ui.model.ProfileAction
import com.kurban.calory.features.profile.ui.model.ProfileEffect
import com.kurban.calory.features.profile.ui.model.ProfileIntent
import com.kurban.calory.features.profile.ui.model.ProfileUiState
import kotlinx.coroutines.flow.SharedFlow

class ProfileComponent(
    val componentContext: ComponentContext,
    private val dependencies: ProfileDependencies,
    val onBack: () -> Unit,
) : ComponentContext by componentContext {
    private val scope = componentScope()

    private val store = Store(
        initialState = ProfileUiState(),
        reducer = profileReducer(),
        middlewares = listOf(
            LoadProfileMiddleware(dependencies.getUserProfileUseCase, dependencies.dispatchers),
            SaveProfileMiddleware(dependencies.saveUserProfileUseCase, dependencies.dispatchers)
        ),
        scope = scope,
        initialActions = listOf(ProfileAction.LoadProfile)
    )

    val state: Value<ProfileUiState> = store.state
    val effects: SharedFlow<ProfileEffect> = store.effects

    fun dispatch(intent: ProfileIntent) {
        store.dispatch(
            when (intent) {
                ProfileIntent.LoadProfile -> ProfileAction.LoadProfile
                is ProfileIntent.NameChanged -> ProfileAction.NameChanged(intent.value)
                is ProfileIntent.SexSelected -> ProfileAction.SexSelected(intent.sex)
                is ProfileIntent.GoalSelected -> ProfileAction.GoalSelected(intent.goal)
                is ProfileIntent.AgeChanged -> ProfileAction.AgeChanged(intent.value)
                is ProfileIntent.HeightChanged -> ProfileAction.HeightChanged(intent.value)
                is ProfileIntent.WeightChanged -> ProfileAction.WeightChanged(intent.value)
                ProfileIntent.Save -> ProfileAction.SaveProfile
                ProfileIntent.ClearError -> ProfileAction.ClearError
            }
        )
    }
}

data class ProfileDependencies(
    val getUserProfileUseCase: GetUserProfileUseCase,
    val saveUserProfileUseCase: SaveUserProfileUseCase,
    val dispatchers: AppDispatchers
)
