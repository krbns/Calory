package com.kurban.calory.features.profile.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.profile.domain.GetUserProfileUseCase
import com.kurban.calory.features.profile.ui.model.ProfileAction
import com.kurban.calory.features.profile.ui.model.ProfileEffect
import com.kurban.calory.features.profile.ui.model.ProfileUiState
import kotlinx.coroutines.withContext

class LoadProfileMiddleware(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val dispatchers: AppDispatchers
) : Middleware<ProfileUiState, ProfileAction, ProfileEffect> {
    override suspend fun invoke(
        action: ProfileAction,
        state: ProfileUiState,
        dispatch: suspend (ProfileAction) -> Unit,
        emitEffect: suspend (ProfileEffect) -> Unit
    ) {
        if (action !is ProfileAction.LoadProfile) return

        try {
            val profile = withContext(dispatchers.io) { getUserProfileUseCase(Unit) }
            dispatch(ProfileAction.LoadProfileSuccess(profile))
        } catch (e: Exception) {
            emitEffect(ProfileEffect.Error(e.message ?: "Не удалось загрузить профиль"))
            dispatch(ProfileAction.LoadProfileFailure(e.message.orEmpty()))
        }
    }
}
