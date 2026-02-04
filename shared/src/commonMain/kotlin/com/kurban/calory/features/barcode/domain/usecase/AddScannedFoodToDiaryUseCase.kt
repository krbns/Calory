package com.kurban.calory.features.barcode.domain.usecase

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.main.domain.TrackedFoodRepository
import com.kurban.calory.features.main.domain.model.TrackedFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.datetime.Clock

class AddScannedFoodToDiaryUseCase(
    private val repository: BarcodeProductRepository,
    private val trackedFoodRepository: TrackedFoodRepository,
    private val dayProvider: DayProvider,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<AddScannedFoodToDiaryUseCase.Params, Unit>(dispatcher) {

    override suspend fun execute(params: Params) {
        if (params.grams <= 0) {
            throw DomainError.ValidationError(originalMessage = "Укажите вес порции")
        }

        val product = params.product
        val factor = params.grams / 100.0
        val dayId = dayProvider.currentDayId()

        val tracked = TrackedFood(
            id = 0L,
            foodId = product.id ?: 0L,
            name = product.name,
            grams = params.grams,
            calories = product.calories * factor,
            proteins = product.proteins * factor,
            fats = product.fats * factor,
            carbs = product.carbs * factor,
            dayId = dayId,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )

        trackedFoodRepository.add(tracked)
        repository.saveProduct(product).getOrThrow()
    }

    data class Params(
        val product: BarcodeProduct,
        val grams: Int
    )
}
