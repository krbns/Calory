package com.kurban.calory.features.barcode.domain.datasource

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct

interface BarcodeProductDataSource {
    suspend fun getProductByBarcode(barcode: String): AppResult<BarcodeProduct?>
    suspend fun saveProduct(product: BarcodeProduct): AppResult<Unit>
    suspend fun updateFavorite(barcode: String, isFavorite: Boolean): AppResult<Unit>
    suspend fun getFavoriteProducts(): AppResult<List<BarcodeProduct>>
    suspend fun deleteExpiredCache(): AppResult<Int>
}