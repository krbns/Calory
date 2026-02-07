package com.kurban.calory.features.onboarding.ui.model

import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserSex

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.NameAge,
    val nameInput: String = "",
    val ageInput: String = "",
    val sex: UserSex = UserSex.MALE,
    val heightInput: String = "",
    val goal: UserGoal = UserGoal.GAIN_MUSCLE,
    val weightInput: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
