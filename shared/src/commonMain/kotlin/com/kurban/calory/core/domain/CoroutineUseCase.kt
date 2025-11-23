package com.kurban.calory.core.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class CoroutineUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(parameters: P): R? = try {
        withContext(coroutineDispatcher) {
            execute(parameters)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    protected abstract suspend fun execute(parameters: P): R
}
