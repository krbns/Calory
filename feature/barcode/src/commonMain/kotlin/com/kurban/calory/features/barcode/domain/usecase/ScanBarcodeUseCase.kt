package com.kurban.calory.features.barcode.domain.usecase

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.barcode.domain.model.ScanResult
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import kotlinx.coroutines.CoroutineDispatcher

class ScanBarcodeUseCase(
    private val repository: BarcodeProductRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<Unit, ScanResult>(dispatcher) {

    override suspend fun execute(params: Unit): ScanResult {
        return repository.scanBarcode()
    }
}