package com.kurban.calory.features.barcode.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.kurban.calory.core.navigation.componentScope
import com.kurban.calory.core.ui.mvi.Store
import com.kurban.calory.features.barcode.domain.usecase.AddScannedFoodToDiaryUseCase
import com.kurban.calory.features.barcode.domain.usecase.ScanBarcodeUseCase
import com.kurban.calory.features.barcode.domain.usecase.SearchProductByBarcodeUseCase
import com.kurban.calory.features.barcode.domain.usecase.ToggleFavoriteUseCase
import com.kurban.calory.features.barcode.ui.logic.AddScannedFoodToDiaryMiddleware
import com.kurban.calory.features.barcode.ui.logic.ScanBarcodeMiddleware
import com.kurban.calory.features.barcode.ui.logic.SearchProductByBarcodeMiddleware
import com.kurban.calory.features.barcode.ui.logic.ToggleFavoriteMiddleware
import com.kurban.calory.features.barcode.ui.logic.barcodeScannerReducer
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerAction
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerEffect
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerIntent
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerUiState
import kotlinx.coroutines.flow.SharedFlow

class BarcodeScannerComponent(
    componentContext: ComponentContext,
    dependencies: BarcodeScannerDependencies,
    val onBack: () -> Unit = {}
) : ComponentContext by componentContext {

    private val scope = componentScope()

    val store = Store(
        initialState = BarcodeScannerUiState(),
        reducer = barcodeScannerReducer(),
        middlewares = listOf(
            ScanBarcodeMiddleware(dependencies.scanBarcodeUseCase),
            SearchProductByBarcodeMiddleware(dependencies.searchProductByBarcodeUseCase),
            AddScannedFoodToDiaryMiddleware(dependencies.addScannedFoodToDiaryUseCase),
            ToggleFavoriteMiddleware(dependencies.toggleFavoriteUseCase)
        ),
        scope = scope,
    )

    val state: Value<BarcodeScannerUiState> = store.state
    val effects: SharedFlow<BarcodeScannerEffect> = store.effects

    fun dispatch(intent: BarcodeScannerIntent) {
        store.dispatch(
            when (intent) {
                is BarcodeScannerIntent.StartScanning -> BarcodeScannerAction.StartScanning
                is BarcodeScannerIntent.StopScanning -> BarcodeScannerAction.StopScanning
                is BarcodeScannerIntent.ProductScanned -> BarcodeScannerAction.SearchProduct(intent.barcode)
                is BarcodeScannerIntent.SearchProduct -> BarcodeScannerAction.SearchProduct(intent.barcode)
                is BarcodeScannerIntent.ClearError -> BarcodeScannerAction.ClearError
                is BarcodeScannerIntent.NavigateBack -> BarcodeScannerAction.NavigateBack
                is BarcodeScannerIntent.AddToDiary -> BarcodeScannerAction.AddToDiary(intent.product, intent.grams)
                is BarcodeScannerIntent.ToggleFavorite -> BarcodeScannerAction.ToggleFavorite(intent.barcode, intent.isFavorite)
                is BarcodeScannerIntent.RequestCameraPermission -> BarcodeScannerAction.RequestCameraPermission
            }
        )
    }
}

data class BarcodeScannerDependencies(
    val scanBarcodeUseCase: ScanBarcodeUseCase,
    val searchProductByBarcodeUseCase: SearchProductByBarcodeUseCase,
    val addScannedFoodToDiaryUseCase: AddScannedFoodToDiaryUseCase,
    val toggleFavoriteUseCase: ToggleFavoriteUseCase,
)
