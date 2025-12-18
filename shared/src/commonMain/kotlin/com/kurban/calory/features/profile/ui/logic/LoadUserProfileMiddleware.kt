package com.kurban.calory.features.profile.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.GetUserProfileUseCase
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainUiState
import kotlinx.coroutines.withContext

class LoadUserProfileMiddleware(
    private val getUserProfile: GetUserProfileUseCase,
    private val calculateMacroTargets: CalculateMacroTargetsUseCase,
    private val dispatchers: AppDispatchers
) : Middleware<MainUiState, MainAction, MainEffect> {

    override suspend fun invoke(
        action: MainAction,
        state: MainUiState,
        dispatch: suspend (MainAction) -> Unit,
        emitEffect: suspend (MainEffect) -> Unit
    ) {
        if (action !is MainAction.LoadProfile) return

        try {
            val profile = withContext(dispatchers.io) { getUserProfile(Unit) }
            val targets = profile?.let { calculateMacroTargets(it) }
            dispatch(MainAction.LoadProfileSuccess(targets))
        } catch (e: Exception) {
            emitEffect(MainEffect.Error(e.message ?: "Не удалось загрузить профиль"))
            dispatch(MainAction.LoadProfileFailure(e.message.orEmpty()))
        }
    }
}
