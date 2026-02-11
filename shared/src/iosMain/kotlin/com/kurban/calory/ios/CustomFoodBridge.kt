package com.kurban.calory.ios

import com.kurban.calory.features.customfood.domain.AddCustomFoodToDiaryUseCase
import com.kurban.calory.features.customfood.domain.CreateCustomFoodUseCase
import com.kurban.calory.features.customfood.domain.ObserveCustomFoodsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.Koin

class CustomFoodBridge internal constructor(
    koin: Koin,
    private val scope: CoroutineScope,
) {
    private val observeCustomFoodsUseCase = koin.get<ObserveCustomFoodsUseCase>()
    private val createCustomFoodUseCase = koin.get<CreateCustomFoodUseCase>()
    private val addCustomFoodToDiaryUseCase = koin.get<AddCustomFoodToDiaryUseCase>()

    fun observeFoods(
        onEach: (List<IosCustomFoodDto>) -> Unit,
        onError: (String) -> Unit,
    ): DisposableHandle {
        return scope.observe(
            flow = observeCustomFoodsUseCase(),
            onEach = { foods -> onEach(foods.map { it.toDto() }) },
            onError = onError,
        )
    }

    fun createFood(
        name: String,
        calories: Double,
        proteins: Double,
        fats: Double,
        carbs: Double,
        onSuccess: (IosCustomFoodDto) -> Unit,
        onError: (String) -> Unit,
    ) {
        scope.launch {
            createCustomFoodUseCase(
                CreateCustomFoodUseCase.Parameters(
                    name = name,
                    calories = calories,
                    proteins = proteins,
                    fats = fats,
                    carbs = carbs,
                )
            ).foldResult(
                onSuccess = { onSuccess(it.toDto()) },
                onError = onError,
            )
        }
    }

    fun addCustomFoodToDiary(
        foodId: Long,
        grams: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        scope.launch {
            addCustomFoodToDiaryUseCase(
                AddCustomFoodToDiaryUseCase.Parameters(foodId = foodId, grams = grams)
            ).foldResult(
                onSuccess = onSuccess,
                onError = onError,
            )
        }
    }
}
