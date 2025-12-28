package com.kurban.calory.features.onboarding.ui.model

import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.domain.model.UserSex

sealed class OnboardingAction {
    data class NameChanged(val value: String) : OnboardingAction()
    data class AgeChanged(val value: String) : OnboardingAction()
    data class HeightChanged(val value: String) : OnboardingAction()
    data class WeightChanged(val value: String) : OnboardingAction()
    data class SexSelected(val sex: UserSex) : OnboardingAction()
    data class GoalSelected(val goal: UserGoal) : OnboardingAction()
    object Next : OnboardingAction()
    object Back : OnboardingAction()
    object Submit : OnboardingAction()
    object SaveStarted : OnboardingAction()
    data class SaveSuccess(val profile: UserProfile) : OnboardingAction()
    data class SaveFailure(val message: String) : OnboardingAction()
    data class ValidationFailed(val message: String) : OnboardingAction()
    object ClearError : OnboardingAction()
}
