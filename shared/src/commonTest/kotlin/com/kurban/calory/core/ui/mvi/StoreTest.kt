package com.kurban.calory.core.ui.mvi

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StoreTest {

    @Test
    fun `Store processes actions in order`() = runTest {
        val store = Store<List<Int>, Int, Unit>(
            initialState = emptyList<Int>(),
            reducer = { state, action -> state + action },
            middlewares = emptyList<Middleware<List<Int>, Int, Unit>>(),
            scope = backgroundScope
        )

        store.dispatch(1)
        store.dispatch(2)
        store.dispatch(3)

        runCurrent()

        assertEquals(listOf(1, 2, 3), store.state.value)
    }

    @Test
    fun `Middleware can dispatch follow-up actions without race`() = runTest {
        val store = Store<List<Int>, Int, Unit>(
            initialState = emptyList<Int>(),
            reducer = { state, action -> state + action },
            middlewares = listOf<Middleware<List<Int>, Int, Unit>>(
                { action, _, dispatch, _ ->
                    if (action == 1) {
                        dispatch(2)
                    }
                }
            ),
            scope = backgroundScope
        )

        store.dispatch(1)

        runCurrent()

        assertEquals(listOf(1, 2), store.state.value)
    }

    @Test
    fun `Long middleware does not block action queue reducer pass`() = runTest {
        val store = Store<List<Int>, Int, Unit>(
            initialState = emptyList<Int>(),
            reducer = { state, action -> state + action },
            middlewares = listOf<Middleware<List<Int>, Int, Unit>>(
                { action, _, dispatch, _ ->
                    if (action == 1) {
                        delay(1_000)
                        dispatch(3)
                    }
                }
            ),
            scope = backgroundScope
        )

        store.dispatch(1)
        store.dispatch(2)

        runCurrent()
        assertEquals(listOf(1, 2), store.state.value)

        advanceTimeBy(1_000)
        runCurrent()
        assertEquals(listOf(1, 2, 3), store.state.value)
    }
}
