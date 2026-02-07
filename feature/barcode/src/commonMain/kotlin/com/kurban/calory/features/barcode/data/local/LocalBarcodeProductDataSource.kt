package com.kurban.calory.features.barcode.data.local

import barcode.BarcodeProductEntity
import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.features.barcode.domain.datasource.BarcodeProductDataSource
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import kotlinx.datetime.Clock

class LocalBarcodeProductDataSource(
    private val database: sqldelight.barcodeProductScheme.barcode.BarcodeProductDatabase,
    private val clock: Clock = Clock.System
) : BarcodeProductDataSource {

    override suspend fun getProductByBarcode(barcode: String): AppResult<BarcodeProduct?> {
        return try {
            val currentTime = clock.now().epochSeconds
            val entity = database.barcodeProductQueries
                .selectByBarcode(barcode, currentTime)
                .executeAsOneOrNull()
            
            AppResult.Success(entity?.toBarcodeProduct())
        } catch (e: Exception) {
            AppResult.Failure(DomainError.fromThrowable(e))
        }
    }

    override suspend fun saveProduct(product: BarcodeProduct): AppResult<Unit> {
        return try {
            database.barcodeProductQueries.insertOrUpdate(
                barcode = product.barcode,
                name = product.name,
                brand = product.brand,
                calories = product.calories,
                proteins = product.proteins,
                fats = product.fats,
                carbs = product.carbs,
                servingSize = product.servingSize.toLong(),
                imageUrl = product.imageUrl,
                isFavorite = if (product.isFavorite) 1L else 0L,
                cachedAt = product.cachedAt,
                expiresAt = product.expiresAt
            )
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(DomainError.fromThrowable(e))
        }
    }

    override suspend fun updateFavorite(barcode: String, isFavorite: Boolean): AppResult<Unit> {
        return try {
            database.barcodeProductQueries.updateFavorite(
                isFavorite = if (isFavorite) 1L else 0L,
                barcode = barcode
            )
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Failure(DomainError.fromThrowable(e))
        }
    }

    override suspend fun getFavoriteProducts(): AppResult<List<BarcodeProduct>> {
        return try {
            val products = database.barcodeProductQueries
                .selectFavorites()
                .executeAsList()
                .map { it.toBarcodeProduct() }
            AppResult.Success(products)
        } catch (e: Exception) {
            AppResult.Failure(DomainError.fromThrowable(e))
        }
    }

    override suspend fun deleteExpiredCache(): AppResult<Int> {
        return try {
            val currentTime = clock.now().epochSeconds
            val deletedCount = database.barcodeProductQueries
                .deleteExpiredCacheCount(currentTime)
                .executeAsOne()
            database.barcodeProductQueries
                .deleteExpiredCache(currentTime)
            AppResult.Success(deletedCount.toInt())
        } catch (e: Exception) {
            AppResult.Failure(DomainError.fromThrowable(e))
        }
    }
}

private fun BarcodeProductEntity.toBarcodeProduct(): BarcodeProduct {
    return BarcodeProduct(
        id = this.id,
        barcode = this.barcode,
        name = this.name,
        brand = this.brand,
        calories = this.calories,
        proteins = this.proteins,
        fats = this.fats,
        carbs = this.carbs,
        servingSize = this.servingSize.toInt(),
        imageUrl = this.imageUrl,
        isFavorite = this.isFavorite == 1L,
        cachedAt = this.cachedAt,
        expiresAt = this.expiresAt
    )
}