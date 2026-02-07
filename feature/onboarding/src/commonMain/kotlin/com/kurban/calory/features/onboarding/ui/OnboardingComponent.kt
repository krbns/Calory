package com.kurban.calory.features.onboarding.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.navigation.componentScope
import com.kurban.calory.core.ui.mvi.Store
import com.kurban.calory.features.onboarding.ui.logic.SaveOnboardingMiddleware
import com.kurban.calory.features.onboarding.ui.logic.onboardingReducer
import com.kurban.calory.features.onboarding.ui.model.OnboardingAction
import com.kurban.calory.features.onboarding.ui.model.OnboardingEffect
import com.kurban.calory.features.onboarding.ui.model.OnboardingIntent
import com.kurban.calory.features.onboarding.ui.model.OnboardingUiState
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserSex
import kotlinx.coroutines.flow.SharedFlow

class OnboardingComponent(
    val componentContext: ComponentContext,
    private val dependencies: OnboardingDependencies,
    val onFinished: () -> Unit
) : ComponentContext by componentContext {

    private val scope = componentScope()

    private val store = Store(
        initialState = OnboardingUiState(),
        reducer = onboardingReducer(),
        middlewares = listOf(
            SaveOnboardingMiddleware(dependencies.saveUserProfileUseCase, dependencies.dispatchers)
        ),
        scope = scope
    )

    val state: Value<OnboardingUiState> = store.state
    val effects: SharedFlow<OnboardingEffect> = store.effects

    fun dispatch(intent: OnboardingIntent) {
        store.dispatch(
            when (intent) {
                is OnboardingIntent.NameChanged -> OnboardingAction.NameChanged(intent.value)
                is OnboardingIntent.AgeChanged -> OnboardingAction.AgeChanged(intent.value)
                is OnboardingIntent.HeightChanged -> OnboardingAction.HeightChanged(intent.value)
                is OnboardingIntent.WeightChanged -> OnboardingAction.WeightChanged(intent.value)
                is OnboardingIntent.SexSelected -> OnboardingAction.SexSelected(intent.sex)
                is OnboardingIntent.GoalSelected -> OnboardingAction.GoalSelected(intent.goal)
                OnboardingIntent.Next -> OnboardingAction.Next
                OnboardingIntent.Back -> OnboardingAction.Back
                OnboardingIntent.Submit -> OnboardingAction.Submit
                OnboardingIntent.ClearError -> OnboardingAction.ClearError
            }
        )
    }
}

data class OnboardingDependencies(
    val saveUserProfileUseCase: SaveUserProfileUseCase,
    val dispatchers: AppDispatchers
)
