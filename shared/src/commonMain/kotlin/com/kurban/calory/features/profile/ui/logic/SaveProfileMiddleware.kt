package com.kurban.calory.features.profile.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
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
            dispatch(ProfileAction.SaveProfileFailure("Введите имя"))
            return
        }
        if (age == null || age <= 0) {
            dispatch(ProfileAction.SaveProfileFailure("Введите возраст"))
            return
        }
        if (height == null || height <= 0) {
            dispatch(ProfileAction.SaveProfileFailure("Введите рост"))
            return
        }
        if (weight == null || weight <= 0.0) {
            dispatch(ProfileAction.SaveProfileFailure("Введите вес"))
            return
        }

        dispatch(ProfileAction.SaveProfileStarted)

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
            dispatch(ProfileAction.SaveProfileSuccess(profile))
        } catch (e: Exception) {
            emitEffect(ProfileEffect.Error(e.message ?: "Не удалось сохранить профиль"))
            dispatch(ProfileAction.SaveProfileFailure(e.message.orEmpty()))
        }
    }
}
