package com.kurban.calory.ios

import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.DeleteTrackedFoodUseCase
import com.kurban.calory.features.main.domain.ObserveTrackedForDayUseCase
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.Koin

class MainBridge internal constructor(
    koin: Koin,
    private val scope: CoroutineScope,
) {
    private val dayProvider = koin.get<DayProvider>()
    private val searchFoodUseCase = koin.get<SearchFoodUseCase>()
    private val addTrackedFoodUseCase = koin.get<AddTrackedFoodUseCase>()
    private val deleteTrackedFoodUseCase = koin.get<DeleteTrackedFoodUseCase>()
    private val observeTrackedForDayUseCase = koin.get<ObserveTrackedForDayUseCase>()
    private val observeUserProfileUseCase = koin.get<ObserveUserProfileUseCase>()
    private val calculateMacroTargetsUseCase = koin.get<CalculateMacroTargetsUseCase>()
    private val calculateTotalsUseCase = koin.get<CalculateTotalsUseCase>()

    val barcodeAvailable: Boolean = false

    fun currentDayId(): String = dayProvider.currentDayId()

    fun observeMainState(
        dayId: String,
        onEach: (IosMainStateDto) -> Unit,
        onError: (String) -> Unit,
    ): DisposableHandle {
        val trackedFlow = observeTrackedForDayUseCase(dayId)
        val targetsFlow = observeUserProfileUseCase().map { profile ->
            profile?.let(calculateMacroTargetsUseCase::invoke)
        }

        return scope.observe(
            flow = combine(trackedFlow, targetsFlow) { tracked, targets ->
                IosMainStateDto(
                    dayId = dayId,
                    trackedFoods = tracked.map { it.toDto() },
                    totals = calculateTotalsUseCase(tracked).toDto(),
                    macroTargets = targets?.toDto(),
                    barcodeAvailable = barcodeAvailable,
                )
            },
            onEach = onEach,
            onError = onError,
        )
    }

    fun searchFoods(
        query: String,
        onSuccess: (List<IosFoodDto>) -> Unit,
        onError: (String) -> Unit,
    ) {
        scope.launch {
            searchFoodUseCase(SearchFoodUseCase.Parameters(query.trim())).foldResult(
                onSuccess = { onSuccess(it.map { food -> food.toDto() }) },
                onError = onError,
            )
        }
    }

    fun addTrackedFood(
        foodName: String,
        grams: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        scope.launch {
            addTrackedFoodUseCase(AddTrackedFoodUseCase.Parameters(foodName, grams)).foldResult(
                onSuccess = onSuccess,
                onError = onError,
            )
        }
    }

    fun deleteTrackedFood(
        entryId: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        scope.launch {
            deleteTrackedFoodUseCase(DeleteTrackedFoodUseCase.Parameters(entryId)).foldResult(
                onSuccess = { onSuccess() },
                onError = onError,
            )
        }
    }
}
