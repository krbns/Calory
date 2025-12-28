package com.kurban.calory.features.onboarding.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.onboarding.ui.model.OnboardingAction
import com.kurban.calory.features.onboarding.ui.model.OnboardingEffect
import com.kurban.calory.features.onboarding.ui.model.OnboardingUiState
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.model.UserProfile
import kotlinx.coroutines.withContext

class SaveOnboardingMiddleware(
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val dispatchers: AppDispatchers
) : Middleware<OnboardingUiState, OnboardingAction, OnboardingEffect> {
    override suspend fun invoke(
        action: OnboardingAction,
        state: OnboardingUiState,
        dispatch: suspend (OnboardingAction) -> Unit,
        emitEffect: suspend (OnboardingEffect) -> Unit
    ) {
        if (action !is OnboardingAction.Submit) return

        val name = state.nameInput.trim()
        val age = state.ageInput.toIntOrNull()
        val height = state.heightInput.toIntOrNull()
        val weight = state.weightInput.replace(',', '.').toDoubleOrNull()

        when {
            name.isEmpty() -> {
                dispatch(OnboardingAction.ValidationFailed("Введите имя"))
                return
            }
            age == null || age <= 0 -> {
                dispatch(OnboardingAction.ValidationFailed("Введите возраст"))
                return
            }
            height == null || height <= 0 -> {
                dispatch(OnboardingAction.ValidationFailed("Введите рост"))
                return
            }
            weight == null || weight <= 0.0 -> {
                dispatch(OnboardingAction.ValidationFailed("Введите вес"))
                return
            }
        }

        dispatch(OnboardingAction.SaveStarted)

        try {
            val profile = UserProfile(
                name = name,
                sex = state.sex,
                age = age,
                heightCm = height,
                weightKg = weight,
                goal = state.goal
            )
            withContext(dispatchers.io) {
                saveUserProfileUseCase(profile)
            }
            dispatch(OnboardingAction.SaveSuccess(profile))
            emitEffect(OnboardingEffect.Completed)
        } catch (e: Exception) {
            val message = e.message.orEmpty().ifEmpty { "Не удалось сохранить профиль" }
            dispatch(OnboardingAction.SaveFailure(message))
            emitEffect(OnboardingEffect.Error(message))
        }
    }
}
