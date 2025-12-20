package com.kurban.calory.features.profile.ui.model

import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserSex

data class ProfileUiState(
    val sex: UserSex = UserSex.MALE,
    val goal: UserGoal = UserGoal.GAIN_MUSCLE,
    val ageInput: String = "",
    val heightInput: String = "",
    val weightInput: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false
)
