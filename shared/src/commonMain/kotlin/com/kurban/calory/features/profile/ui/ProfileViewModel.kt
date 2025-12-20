package com.kurban.calory.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurban.calory.core.domain.AppDispatchers
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
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    getUserProfileUseCase: GetUserProfileUseCase,
    saveUserProfileUseCase: SaveUserProfileUseCase,
    dispatchers: AppDispatchers
) : ViewModel() {

    private val store = Store(
        initialState = ProfileUiState(),
        reducer = profileReducer(),
        middlewares = listOf(
            LoadProfileMiddleware(getUserProfileUseCase, dispatchers),
            SaveProfileMiddleware(saveUserProfileUseCase, dispatchers)
        ),
        scope = viewModelScope,
        initialActions = listOf(ProfileAction.LoadProfile)
    )

    val uiState: StateFlow<ProfileUiState> = store.state
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
