package com.kurban.calory.features.main.ui.logic

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.mvi.Middleware
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.ui.model.MainAction
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchMiddleware(
    private val searchFood: SearchFoodUseCase,
    private val dispatchers: AppDispatchers,
    private val scope: CoroutineScope
) : Middleware<MainUiState, MainAction, MainEffect> {

    private var searchJob: Job? = null

    override suspend fun invoke(
        action: MainAction,
        state: MainUiState,
        dispatch: suspend (MainAction) -> Unit,
        emitEffect: suspend (MainEffect) -> Unit
    ) {
        if (action !is MainAction.QueryChanged) return

        if (action.query.isBlank()) {
            dispatch(MainAction.SearchSuccess(emptyList()))
            return
        }
        searchJob?.cancel()
        searchJob = scope.launch(dispatchers.io) {
            try {
                val result = searchFood(SearchFoodUseCase.Parameters(action.query)) ?: emptyList()
                dispatch(MainAction.SearchSuccess(result))
            } catch (e: Exception) {
                emitEffect(MainEffect.Error(e.message ?: "Ошибка поиска"))
                dispatch(MainAction.SearchFailure(e.message.orEmpty()))
            }
        }
    }
}
