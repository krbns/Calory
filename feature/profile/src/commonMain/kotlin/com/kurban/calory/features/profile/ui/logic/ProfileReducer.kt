package com.kurban.calory.features.profile.ui.logic

import com.kurban.calory.core.ui.mvi.Reducer
import com.kurban.calory.features.profile.ui.model.ProfileAction
import com.kurban.calory.features.profile.ui.model.ProfileUiState

fun profileReducer(): Reducer<ProfileUiState, ProfileAction> = { state, action ->
    when (action) {
        is ProfileAction.NameChanged -> state.copy(nameInput = action.value, saved = false)
        is ProfileAction.SexSelected -> state.copy(sex = action.sex, saved = false)
        is ProfileAction.GoalSelected -> state.copy(goal = action.goal, saved = false)
        is ProfileAction.AgeChanged -> state.copy(ageInput = action.value.filter(Char::isDigit), saved = false)
        is ProfileAction.HeightChanged -> state.copy(heightInput = action.value.filter(Char::isDigit), saved = false)
        is ProfileAction.WeightChanged -> {
            val filtered = action.value.replace(',', '.').filter { it.isDigit() || it == '.' }
            state.copy(weightInput = filtered, saved = false)
        }
        is ProfileAction.LoadProfile -> state.copy(isLoading = true, error = null)
        is ProfileAction.LoadProfileSuccess -> {
            val profile = action.profile
            if (profile != null) {
                state.copy(
                    isLoading = false,
                    sex = profile.sex,
                    goal = profile.goal,
                    nameInput = profile.name,
                    ageInput = profile.age.toString(),
                    heightInput = profile.heightCm.toString(),
                    weightInput = profile.weightKg.toString(),
                    error = null
                )
            } else {
                state.copy(isLoading = false, error = null)
            }
        }
        is ProfileAction.LoadProfileFailure -> state.copy(isLoading = false, error = action.error)
        ProfileAction.SaveProfileStarted -> state.copy(isSaving = true, error = null, saved = false)
        is ProfileAction.SaveProfileSuccess -> state.copy(
            isSaving = false,
            error = null,
            saved = true,
            nameInput = action.profile.name,
            sex = action.profile.sex,
            goal = action.profile.goal,
            ageInput = action.profile.age.toString(),
            heightInput = action.profile.heightCm.toString(),
            weightInput = action.profile.weightKg.toString()
        )
        is ProfileAction.SaveProfileFailure -> state.copy(isSaving = false, error = action.error, saved = false)
        ProfileAction.ClearError -> state.copy(error = null)
        else -> state
    }
}
