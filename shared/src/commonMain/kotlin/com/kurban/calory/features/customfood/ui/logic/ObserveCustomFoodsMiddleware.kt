package com.kurban.calory.features.customfood.ui.logic

import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.customfood.domain.ObserveCustomFoodsUseCase
import com.kurban.calory.features.customfood.ui.model.CustomFoodAction
import com.kurban.calory.features.customfood.ui.model.CustomFoodEffect
import com.kurban.calory.features.customfood.ui.model.CustomFoodUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ObserveCustomFoodsMiddleware(
    private val observeCustomFoods: ObserveCustomFoodsUseCase,
    private val scope: CoroutineScope
) : Middleware<CustomFoodUiState, CustomFoodAction, CustomFoodEffect> {

    private var observeJob: Job? = null

    override suspend fun invoke(
        action: CustomFoodAction,
        state: CustomFoodUiState,
        dispatch: suspend (CustomFoodAction) -> Unit,
        emitEffect: suspend (CustomFoodEffect) -> Unit
    ) {
        if (action != CustomFoodAction.ObserveFoods) return

        if (observeJob?.isActive == true) return

        observeJob?.cancel()
        observeJob = scope.launch {
            try {
                observeCustomFoods()
                    .catch {
                        val message = it.message ?: "Не удалось загрузить список"
                        dispatch(CustomFoodAction.FoodsFailed(message))
                        emitEffect(CustomFoodEffect.Error(message))
                    }
                    .collect { foods -> dispatch(CustomFoodAction.FoodsUpdated(foods)) }
            } finally {
                observeJob = null
            }
        }
    }
}
