package com.kurban.calory.features.profile.ui.logic

import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainUiState
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ObserveUserProfileMiddleware(
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val calculateMacroTargetsUseCase: CalculateMacroTargetsUseCase,
    private val scope: CoroutineScope
) : Middleware<MainUiState, MainAction, MainEffect> {

    private var observeJob: Job? = null

    override suspend fun invoke(
        action: MainAction,
        state: MainUiState,
        dispatch: suspend (MainAction) -> Unit,
        emitEffect: suspend (MainEffect) -> Unit
    ) {
        if (action !is MainAction.ObserveProfile) return
        if (observeJob != null) return

        observeJob = scope.launch {
            observeUserProfileUseCase()
                .map { profile -> profile?.let { calculateMacroTargetsUseCase(it) } }
                .catch {
                    val error = DomainError.fromThrowable(it)
                    emitEffect(MainEffect.Error(error))
                }
                .collect { targets -> dispatch(MainAction.LoadProfileSuccess(targets)) }
        }
    }
}
