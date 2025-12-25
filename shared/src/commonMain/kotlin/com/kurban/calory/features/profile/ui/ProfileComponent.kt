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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileComponent(
    val componentContext: ComponentContext,
    val onBack: () -> Unit,
) : ComponentContext by componentContext, KoinComponent {

    private val getUserProfileUseCase: GetUserProfileUseCase by inject()
    private val saveUserProfileUseCase: SaveUserProfileUseCase by inject()
    private val dispatchers: AppDispatchers by inject()
    private val scope = componentScope()

    private val store = Store(
        initialState = ProfileUiState(),
        reducer = profileReducer(),
        middlewares = listOf(
            LoadProfileMiddleware(getUserProfileUseCase, dispatchers),
            SaveProfileMiddleware(saveUserProfileUseCase, dispatchers)
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
