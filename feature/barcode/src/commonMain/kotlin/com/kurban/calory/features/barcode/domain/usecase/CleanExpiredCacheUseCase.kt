package com.kurban.calory.features.barcode.domain.usecase

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import kotlinx.coroutines.CoroutineDispatcher

class CleanExpiredCacheUseCase(
    private val repository: BarcodeProductRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, Int>(dispatcher) {

    override suspend fun execute(params: Unit): Int {
        val result = repository.cleanExpiredCache()
        return result.getOrNull() ?: 0
    }
}