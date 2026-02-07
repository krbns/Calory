package com.kurban.calory.features.barcode.di

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.features.barcode.data.DefaultBarcodeProductRepository
import com.kurban.calory.features.barcode.data.local.LocalBarcodeProductDataSource
import com.kurban.calory.features.barcode.data.remote.RemoteBarcodeProductDataSource
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import com.kurban.calory.features.barcode.domain.datasource.BarcodeProductDataSource
import com.kurban.calory.features.barcode.domain.scanner.BarcodeScannerFactory
import com.kurban.calory.features.barcode.domain.usecase.AddScannedFoodToDiaryUseCase
import com.kurban.calory.features.barcode.domain.usecase.CleanExpiredCacheUseCase
import com.kurban.calory.features.barcode.domain.usecase.GetFavoriteProductsUseCase
import com.kurban.calory.features.barcode.domain.usecase.ScanBarcodeUseCase
import com.kurban.calory.features.barcode.domain.usecase.SearchProductByBarcodeUseCase
import com.kurban.calory.features.barcode.domain.usecase.ToggleFavoriteUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sqldelight.barcodeProductScheme.barcode.BarcodeProductDatabase

private const val LOCAL_DATA_SOURCE = "barcode_local_data_source"
private const val REMOTE_DATA_SOURCE = "barcode_remote_data_source"

val featureBarcodeModule = module {
    single { BarcodeScannerFactory.create() }

    single<BarcodeProductDataSource>(named(LOCAL_DATA_SOURCE)) {
        LocalBarcodeProductDataSource(get<BarcodeProductDatabase>())
    }
    single<BarcodeProductDataSource>(named(REMOTE_DATA_SOURCE)) { RemoteBarcodeProductDataSource() }
    single<BarcodeProductRepository> {
        DefaultBarcodeProductRepository(
            localDataSource = get(named(LOCAL_DATA_SOURCE)),
            remoteDataSource = get(named(REMOTE_DATA_SOURCE)),
            scanner = get()
        )
    }

    factory { SearchProductByBarcodeUseCase(get(), get<AppDispatchers>().io) }
    factory { ScanBarcodeUseCase(get(), get<AppDispatchers>().main) }
    factory { AddScannedFoodToDiaryUseCase(get(), get(), get(), get<AppDispatchers>().io) }
    factory { ToggleFavoriteUseCase(get(), get<AppDispatchers>().io) }
    factory { CleanExpiredCacheUseCase(get(), get<AppDispatchers>().io) }
    factory { GetFavoriteProductsUseCase(get(), get<AppDispatchers>().io) }
}
