package com.kurban.calory.features.barcode.data.remote

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.features.barcode.domain.datasource.BarcodeProductDataSource
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.barcode.data.remote.api.OpenFoodFactsApi
import com.kurban.calory.features.barcode.data.remote.mapper.OpenFoodFactsProductMapper
import kotlinx.datetime.Clock

class RemoteBarcodeProductDataSource(
    private val api: OpenFoodFactsApi = OpenFoodFactsApi(),
    private val mapper: OpenFoodFactsProductMapper = OpenFoodFactsProductMapper(),
    private val clock: Clock = Clock.System
) : BarcodeProductDataSource {
    
    override suspend fun getProductByBarcode(barcode: String): AppResult<BarcodeProduct?> {
        return try {
            val apiResult = api.getProductByBarcode(barcode)
            
            if (apiResult.isFailure) {
                val exception = apiResult.exceptionOrNull()!!
                val domainError = when {
                    exception.message?.contains("Product not found") == true -> 
                        DomainError.NotFound("Продукт не найден", exception)
                    exception.message?.contains("Network error") == true -> 
                        DomainError.NetworkError("Проблема с интернетом", exception)
                    else -> 
                        DomainError.UnknownError("Ошибка при загрузке продукта", exception)
                }
                return AppResult.Failure(domainError)
            }
            
            val response = apiResult.getOrThrow()
            val product = response.product
            
            if (product == null) {
                AppResult.Success(null)
            } else {
                val barcodeProduct = mapper.mapToBarcodeProduct(barcode, product, clock)
                if (barcodeProduct != null) {
                    AppResult.Success(barcodeProduct)
                } else {
                    AppResult.Failure(DomainError.ValidationError("Некорректные данные продукта"))
                }
            }
        } catch (e: Exception) {
            AppResult.Failure(DomainError.fromThrowable(e))
        }
    }

    override suspend fun saveProduct(product: BarcodeProduct): AppResult<Unit> {
        // Open Food Facts is read-only for anonymous users
        // This could be implemented later with user authentication
        return AppResult.Success(Unit)
    }

    override suspend fun updateFavorite(barcode: String, isFavorite: Boolean): AppResult<Unit> {
        // Could sync with remote backend in future
        return AppResult.Success(Unit)
    }

    override suspend fun getFavoriteProducts(): AppResult<List<BarcodeProduct>> {
        // Not applicable for remote data source
        return AppResult.Success(emptyList())
    }

    override suspend fun deleteExpiredCache(): AppResult<Int> {
        // Not applicable for remote data source
        return AppResult.Success(0)
    }
}