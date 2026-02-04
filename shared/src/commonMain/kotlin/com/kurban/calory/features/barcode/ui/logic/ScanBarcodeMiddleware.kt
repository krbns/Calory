package com.kurban.calory.features.barcode.ui.logic

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.core.ui.mvi.Middleware
import com.kurban.calory.features.barcode.domain.model.ScanResult
import com.kurban.calory.features.barcode.domain.usecase.ScanBarcodeUseCase
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerAction
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerEffect
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job

class ScanBarcodeMiddleware(
    private val scanBarcodeUseCase: ScanBarcodeUseCase
) : Middleware<BarcodeScannerUiState, BarcodeScannerAction, BarcodeScannerEffect> {

    private var scanJob: Job? = null

    override suspend fun invoke(
        action: BarcodeScannerAction,
        state: BarcodeScannerUiState,
        dispatch: suspend (BarcodeScannerAction) -> Unit,
        emitEffect: suspend (BarcodeScannerEffect) -> Unit
    ) {
        when (action) {
            is BarcodeScannerAction.StartScanning -> {
                if (scanJob?.isActive == true) return
                scanJob = currentCoroutineContext().job
                try {
                    when (val result = scanBarcodeUseCase(Unit)) {
                        is AppResult.Success -> {
                            if (!currentCoroutineContext().isActive) return
                            dispatch(BarcodeScannerAction.ScanCompleted(result.value))
                            if (result.value is ScanResult.Success) {
                                dispatch(BarcodeScannerAction.SearchProduct(result.value.barcode))
                            }
                        }

                        is AppResult.Failure -> {
                            if (!currentCoroutineContext().isActive) return
                            dispatch(
                                BarcodeScannerAction.ScanCompleted(
                                    ScanResult.Error(
                                        "Failed to scan: ${result.error.message}",
                                        result.error.cause
                                    )
                                )
                            )
                        }
                    }
                } finally {
                    scanJob = null
                }
            }
            
            is BarcodeScannerAction.StopScanning -> {
                scanJob?.cancel()
                scanJob = null
                dispatch(BarcodeScannerAction.ScanCompleted(ScanResult.Cancelled))
            }
            else -> {}
        }
    }
}
