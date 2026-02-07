package com.kurban.calory.features.barcode.data

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.domain.DomainError
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import com.kurban.calory.features.barcode.domain.datasource.BarcodeProductDataSource
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.barcode.domain.model.BarcodeSearchResult
import com.kurban.calory.features.barcode.domain.model.DataSource
import com.kurban.calory.features.barcode.domain.model.ScanResult
import com.kurban.calory.features.barcode.domain.scanner.BarcodeScanner

class DefaultBarcodeProductRepository(
    private val localDataSource: BarcodeProductDataSource,
    private val remoteDataSource: BarcodeProductDataSource,
    private val scanner: BarcodeScanner
) : BarcodeProductRepository {

    override suspend fun scanBarcode(): ScanResult {
        return try {
            if (!scanner.isSupported()) {
                ScanResult.NotSupported
            } else {
                scanner.startScanning()
            }
        } catch (e: Exception) {
            ScanResult.Error("Failed to scan barcode: ${e.message}", e)
        } finally {
            scanner.release()
        }
    }

    override suspend fun getProductByBarcode(barcode: String): BarcodeSearchResult {
        // Step 1: Check local cache first
        when (val localResult = localDataSource.getProductByBarcode(barcode)) {
            is AppResult.Success -> {
                localResult.value?.let { product ->
                    return BarcodeSearchResult.Success(product, DataSource.CACHE)
                }
            }
            is AppResult.Failure -> {
                // Continue to remote if local fails
            }
        }
        
        // Step 2: Try remote API
        when (val remoteResult = remoteDataSource.getProductByBarcode(barcode)) {
            is AppResult.Success -> {
                remoteResult.value?.let { product ->
                    // Save to local cache for future use
                    localDataSource.saveProduct(product)
                    return BarcodeSearchResult.Success(product, DataSource.REMOTE)
                } ?: run {
                    return BarcodeSearchResult.NotFound(barcode)
                }
            }
            is AppResult.Failure -> {
                return when (remoteResult.error) {
                    is DomainError.NotFound -> {
                        BarcodeSearchResult.NotFound(barcode)
                    }
                    is DomainError.NetworkError -> {
                        BarcodeSearchResult.Error(barcode, remoteResult.error.cause ?: Exception("Network error"))
                    }
                    else -> {
                        BarcodeSearchResult.Error(barcode, remoteResult.error.cause ?: Exception("Unknown error"))
                    }
                }
            }
        }
        return BarcodeSearchResult.NotFound(barcode)
    }

    override suspend fun saveProduct(product: BarcodeProduct): AppResult<Unit> {
        // Save to local cache and sync with remote if needed
        val localResult = localDataSource.saveProduct(product)
        val remoteResult = remoteDataSource.saveProduct(product)

        return when {
            localResult is AppResult.Failure -> AppResult.Failure(localResult.error)
            remoteResult is AppResult.Failure -> AppResult.Failure(remoteResult.error)
            else -> AppResult.Success(Unit)
        }
    }

    override suspend fun toggleFavorite(barcode: String, isFavorite: Boolean): AppResult<Unit> {
        // Update local and sync with remote
        val localResult = localDataSource.updateFavorite(barcode, isFavorite)
        val remoteResult = remoteDataSource.updateFavorite(barcode, isFavorite)

        return when {
            localResult is AppResult.Failure -> AppResult.Failure(localResult.error)
            remoteResult is AppResult.Failure -> AppResult.Failure(remoteResult.error)
            else -> AppResult.Success(Unit)
        }
    }

    override suspend fun getFavoriteProducts(): AppResult<List<BarcodeProduct>> {
        return localDataSource.getFavoriteProducts()
    }

    override suspend fun cleanExpiredCache(): AppResult<Int> {
        return localDataSource.deleteExpiredCache()
    }

}
