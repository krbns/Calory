package com.kurban.calory.features.barcode.domain.usecase

import com.kurban.calory.core.domain.CoroutineUseCase
import com.kurban.calory.features.barcode.domain.model.BarcodeSearchResult
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import kotlinx.coroutines.CoroutineDispatcher

class SearchProductByBarcodeUseCase(
    private val repository: BarcodeProductRepository,
    dispatcher: CoroutineDispatcher
) : CoroutineUseCase<SearchProductByBarcodeUseCase.Params, BarcodeSearchResult>(dispatcher) {

    override suspend fun execute(params: Params): BarcodeSearchResult {
        return repository.getProductByBarcode(params.barcode)
    }

    data class Params(val barcode: String)
}