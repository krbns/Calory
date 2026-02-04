package com.kurban.calory.features.barcode.domain.usecase

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetFavoriteProductsUseCase(
    private val repository: BarcodeProductRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, List<BarcodeProduct>>(dispatcher) {

    override suspend fun execute(params: Unit): List<BarcodeProduct> {
        val result = repository.getFavoriteProducts()
        return result.getOrNull() ?: emptyList()
    }
}