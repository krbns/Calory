package com.kurban.calory.features.barcode.domain

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.barcode.domain.model.BarcodeSearchResult
import com.kurban.calory.features.barcode.domain.model.ScanResult

interface BarcodeProductRepository {
    suspend fun scanBarcode(): ScanResult
    suspend fun getProductByBarcode(barcode: String): BarcodeSearchResult
    suspend fun saveProduct(product: BarcodeProduct): AppResult<Unit>
    suspend fun toggleFavorite(barcode: String, isFavorite: Boolean): AppResult<Unit>
    suspend fun getFavoriteProducts(): AppResult<List<BarcodeProduct>>
    suspend fun cleanExpiredCache(): AppResult<Int>
}