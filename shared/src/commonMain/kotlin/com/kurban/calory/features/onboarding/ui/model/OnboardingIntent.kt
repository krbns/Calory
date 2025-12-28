package com.kurban.calory.features.onboarding.ui.model

import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserSex

sealed class OnboardingIntent {
    data class NameChanged(val value: String) : OnboardingIntent()
    data class AgeChanged(val value: String) : OnboardingIntent()
    data class HeightChanged(val value: String) : OnboardingIntent()
    data class WeightChanged(val value: String) : OnboardingIntent()
    data class SexSelected(val sex: UserSex) : OnboardingIntent()
    data class GoalSelected(val goal: UserGoal) : OnboardingIntent()
    object Next : OnboardingIntent()
    object Back : OnboardingIntent()
    object Submit : OnboardingIntent()
    object ClearError : OnboardingIntent()
}
