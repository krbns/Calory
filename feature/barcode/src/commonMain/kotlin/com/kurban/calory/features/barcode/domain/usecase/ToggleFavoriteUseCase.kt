package com.kurban.calory.features.barcode.domain.usecase

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import kotlinx.coroutines.CoroutineDispatcher

class ToggleFavoriteUseCase(
    private val repository: BarcodeProductRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<ToggleFavoriteUseCase.Params, Unit>(dispatcher) {

    override suspend fun execute(params: Params) {
        val result = repository.toggleFavorite(params.barcode, params.isFavorite)
        result.getOrThrow()
    }

    data class Params(
        val barcode: String,
        val isFavorite: Boolean
    )
}