package com.kurban.calory.features.profile.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.model.UserProfile
import com.kurban.calory.features.profile.ui.model.ProfileAction
import com.kurban.calory.features.profile.ui.model.ProfileEffect
import com.kurban.calory.features.profile.ui.model.ProfileUiState
import kotlinx.coroutines.withContext

class SaveProfileMiddleware(
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val dispatchers: AppDispatchers
) : Middleware<ProfileUiState, ProfileAction, ProfileEffect> {

    override suspend fun invoke(
        action: ProfileAction,
        state: ProfileUiState,
        dispatch: suspend (ProfileAction) -> Unit,
        emitEffect: suspend (ProfileEffect) -> Unit
    ) {
        if (action !is ProfileAction.SaveProfile) return

        val name = state.nameInput.trim()
        val age = state.ageInput.toIntOrNull()
        val height = state.heightInput.toIntOrNull()
        val weight = state.weightInput.replace(',', '.').toDoubleOrNull()

        if (name.isEmpty()) {
            val error = DomainError.ValidationError(originalMessage = "Введите имя")
            dispatch(ProfileAction.SaveProfileFailure(error))
            emitEffect(ProfileEffect.Error(error))
            return
        }
        if (age == null || age <= 0) {
            val error = DomainError.ValidationError(originalMessage = "Введите возраст")
            dispatch(ProfileAction.SaveProfileFailure(error))
            emitEffect(ProfileEffect.Error(error))
            return
        }
        if (height == null || height <= 0) {
            val error = DomainError.ValidationError(originalMessage = "Введите рост")
            dispatch(ProfileAction.SaveProfileFailure(error))
            emitEffect(ProfileEffect.Error(error))
            return
        }
        if (weight == null || weight <= 0.0) {
            val error = DomainError.ValidationError(originalMessage = "Введите вес")
            dispatch(ProfileAction.SaveProfileFailure(error))
            emitEffect(ProfileEffect.Error(error))
            return
        }

        dispatch(ProfileAction.SaveProfileStarted)

        val profile = UserProfile(
            name = name,
            sex = state.sex,
            age = age,
            heightCm = height,
            weightKg = weight,
            goal = state.goal
        )

        val result = withContext(dispatchers.io) {
            saveUserProfileUseCase(profile)
        }

        when (result) {
            is AppResult.Success -> {
                dispatch(ProfileAction.SaveProfileSuccess(profile))
            }
            is AppResult.Failure -> {
                emitEffect(ProfileEffect.Error(result.error))
                dispatch(ProfileAction.SaveProfileFailure(result.error))
            }
        }
    }
}
