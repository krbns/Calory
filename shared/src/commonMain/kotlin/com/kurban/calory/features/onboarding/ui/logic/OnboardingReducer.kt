package com.kurban.calory.features.onboarding.ui.logic

import com.kurban.calory.core.ui.mvi.Reducer
import com.kurban.calory.features.onboarding.ui.model.OnboardingAction
import com.kurban.calory.features.onboarding.ui.model.OnboardingStep
import com.kurban.calory.features.onboarding.ui.model.OnboardingUiState

fun onboardingReducer(): Reducer<OnboardingUiState, OnboardingAction> = { state, action ->
    when (action) {
        is OnboardingAction.NameChanged -> state.copy(nameInput = action.value, errorMessage = null)
        is OnboardingAction.AgeChanged -> state.copy(ageInput = action.value.filter(Char::isDigit), errorMessage = null)
        is OnboardingAction.HeightChanged -> state.copy(heightInput = action.value.filter(Char::isDigit), errorMessage = null)
        is OnboardingAction.WeightChanged -> {
            val filtered = action.value.replace(',', '.').filter { it.isDigit() || it == '.' }
            state.copy(weightInput = filtered, errorMessage = null)
        }
        is OnboardingAction.SexSelected -> state.copy(sex = action.sex, errorMessage = null)
        is OnboardingAction.GoalSelected -> state.copy(goal = action.goal, errorMessage = null)
        OnboardingAction.Next -> {
            val error = validateStep(state)
            if (error != null) {
                state.copy(errorMessage = error)
            } else {
                val nextStep = if (state.step == OnboardingStep.Goal) OnboardingStep.Goal else state.step.next()
                state.copy(step = nextStep, errorMessage = null)
            }
        }
        OnboardingAction.Back -> state.copy(step = state.step.previous(), errorMessage = null)
        OnboardingAction.SaveStarted -> state.copy(isSaving = true, errorMessage = null)
        is OnboardingAction.SaveSuccess -> state.copy(isSaving = false, errorMessage = null)
        is OnboardingAction.SaveFailure -> state.copy(isSaving = false, errorMessage = action.message)
        is OnboardingAction.ValidationFailed -> state.copy(errorMessage = action.message, isSaving = false)
        OnboardingAction.ClearError -> state.copy(errorMessage = null)
        OnboardingAction.Submit -> state
    }
}

private fun validateStep(state: OnboardingUiState): String? {
    return when (state.step) {
        OnboardingStep.NameAge -> {
            when {
                state.nameInput.trim().isEmpty() -> "Введите имя"
                state.ageInput.toIntOrNull().let { it == null || it <= 0 } -> "Введите возраст"
                else -> null
            }
        }

        OnboardingStep.Body -> {
            when {
                state.heightInput.toIntOrNull().let { it == null || it <= 0 } -> "Введите рост"
                else -> null
            }
        }

        OnboardingStep.Goal -> {
            when {
                state.weightInput.replace(',', '.').toDoubleOrNull().let { it == null || it <= 0.0 } -> "Введите вес"
                else -> null
            }
        }
    }
}
