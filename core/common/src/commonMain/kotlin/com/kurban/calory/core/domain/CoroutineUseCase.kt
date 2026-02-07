package com.kurban.calory.core.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class CoroutineUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(parameters: P): AppResult<R> = try {
        withContext(coroutineDispatcher) {
            AppResult.success(execute(parameters))
        }
    } catch (e: Exception) {
        AppResult.failure(DomainError.fromThrowable(e))
    }

    protected abstract suspend fun execute(parameters: P): R
}
