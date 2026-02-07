package com.kurban.calory.features.onboarding.ui.model

sealed class OnboardingEffect {
    object Completed : OnboardingEffect()
    data class Error(val message: String) : OnboardingEffect()
}
