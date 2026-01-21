package com.kurban.calory.features.profile.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.domain.AppResult
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

        val result = withContext(dispatchers.io) { getUserProfileUseCase(Unit) }

        when (result) {
            is AppResult.Success -> {
                dispatch(ProfileAction.LoadProfileSuccess(result.value))
            }
            is AppResult.Failure -> {
                emitEffect(ProfileEffect.Error(result.error))
                dispatch(ProfileAction.LoadProfileFailure(result.error))
            }
        }
    }
}
