package com.kurban.calory.features.barcode.ui

import com.kurban.calory.core.domain.AppResult
import com.kurban.calory.features.barcode.domain.BarcodeProductRepository
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.barcode.domain.model.BarcodeSearchResult
import com.kurban.calory.features.barcode.domain.model.ScanResult
import com.kurban.calory.features.barcode.domain.usecase.ScanBarcodeUseCase
import com.kurban.calory.features.barcode.ui.logic.ScanBarcodeMiddleware
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerAction
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScanBarcodeMiddlewareTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun `Barcode Start then Stop emits cancelled path`() = runTest(dispatcher) {
        val middleware = ScanBarcodeMiddleware(
            scanBarcodeUseCase = ScanBarcodeUseCase(
                repository = FakeBarcodeRepository { delay(60_000); ScanResult.Success("123456") },
                dispatcher = dispatcher
            )
        )
        val actions = Channel<BarcodeScannerAction>(Channel.UNLIMITED)

        val startJob = launch {
            middleware.invoke(
                BarcodeScannerAction.StartScanning,
                BarcodeScannerUiState(),
                dispatch = { actions.send(it) },
                emitEffect = {}
            )
        }

        runCurrent()

        middleware.invoke(
            BarcodeScannerAction.StopScanning,
            BarcodeScannerUiState(isScanning = true),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val cancelledAction = actions.receive()
        assertEquals(
            BarcodeScannerAction.ScanCompleted(ScanResult.Cancelled),
            cancelledAction
        )

        startJob.join()
    }

    @Test
    fun `Barcode success chain dispatches search`() = runTest(dispatcher) {
        val middleware = ScanBarcodeMiddleware(
            scanBarcodeUseCase = ScanBarcodeUseCase(
                repository = FakeBarcodeRepository { ScanResult.Success("4607004652964") },
                dispatcher = dispatcher
            )
        )
        val actions = Channel<BarcodeScannerAction>(Channel.UNLIMITED)

        middleware.invoke(
            BarcodeScannerAction.StartScanning,
            BarcodeScannerUiState(),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val firstAction = actions.receive()
        val secondAction = actions.receive()

        assertEquals(
            BarcodeScannerAction.ScanCompleted(ScanResult.Success("4607004652964")),
            firstAction
        )
        assertEquals(
            BarcodeScannerAction.SearchProduct("4607004652964"),
            secondAction
        )
    }

    @Test
    fun `Barcode scan failure dispatches error result`() = runTest(dispatcher) {
        val middleware = ScanBarcodeMiddleware(
            scanBarcodeUseCase = ScanBarcodeUseCase(
                repository = FakeBarcodeRepository { throw IllegalStateException("boom") },
                dispatcher = dispatcher
            )
        )
        val actions = Channel<BarcodeScannerAction>(Channel.UNLIMITED)

        middleware.invoke(
            BarcodeScannerAction.StartScanning,
            BarcodeScannerUiState(),
            dispatch = { actions.send(it) },
            emitEffect = {}
        )

        val action = actions.receive()
        assertTrue(action is BarcodeScannerAction.ScanCompleted)
        assertTrue(action.result is ScanResult.Error)
        assertEquals("Failed to scan: boom", (action.result as ScanResult.Error).message)
    }
}

private class FakeBarcodeRepository(
    private val scan: suspend () -> ScanResult
) : BarcodeProductRepository {
    override suspend fun scanBarcode(): ScanResult = scan()

    override suspend fun getProductByBarcode(barcode: String): BarcodeSearchResult {
        error("Not used in tests")
    }

    override suspend fun saveProduct(product: BarcodeProduct): AppResult<Unit> {
        error("Not used in tests")
    }

    override suspend fun toggleFavorite(barcode: String, isFavorite: Boolean): AppResult<Unit> {
        error("Not used in tests")
    }

    override suspend fun getFavoriteProducts(): AppResult<List<BarcodeProduct>> {
        error("Not used in tests")
    }

    override suspend fun cleanExpiredCache(): AppResult<Int> {
        error("Not used in tests")
    }
}
