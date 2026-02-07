package com.kurban.calory.core.ui.mvi

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

typealias Reducer<S, A> = (S, A) -> S

typealias Middleware<S, A, E> = suspend (
    action: A,
    state: S,
    dispatch: suspend (A) -> Unit,
    emitEffect: suspend (E) -> Unit
) -> Unit

class Store<S : Any, A, E>(
    initialState: S,
    private val reducer: Reducer<S, A>,
    private val middlewares: List<Middleware<S, A, E>>,
    private val scope: CoroutineScope,
    initialActions: List<A> = emptyList(),
) {
    private val actionQueue = Channel<A>(Channel.UNLIMITED)

    private val _state: MutableValue<S> = MutableValue(initialState)
    val state: Value<S> get() = _state

    private val _effects = MutableSharedFlow<E>(replay = 0)
    val effects: SharedFlow<E> = _effects.asSharedFlow()

    init {
        scope.launch {
            for (action in actionQueue) {
                val newState = reducer(_state.value, action)
                _state.value = newState

                // Run middleware asynchronously so long-running tasks
                // don't block sequential reducer processing.
                scope.launch {
                    middlewares.forEach { middleware ->
                        middleware(action, newState, { dispatch(it) }, { emitEffect(it) })
                    }
                }
            }
        }

        initialActions.forEach { dispatch(it) }
    }

    fun dispatch(action: A) {
        if (!actionQueue.trySend(action).isSuccess) {
            scope.launch {
                actionQueue.send(action)
            }
        }
    }

    private suspend fun emitEffect(effect: E) {
        _effects.emit(effect)
    }
}
