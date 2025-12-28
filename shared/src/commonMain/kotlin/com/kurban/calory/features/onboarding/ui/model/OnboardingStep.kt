package com.kurban.calory.features.onboarding.ui.model

enum class OnboardingStep {
    NameAge,
    Body,
    Goal;

    fun next(): OnboardingStep = when (this) {
        NameAge -> Body
        Body -> Goal
        Goal -> Goal
    }

    fun previous(): OnboardingStep = when (this) {
        NameAge -> NameAge
        Body -> NameAge
        Goal -> Body
    }
}
