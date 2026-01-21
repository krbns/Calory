package com.kurban.calory.core.domain

sealed class AppResult<out T> {
    data class Success<out T>(val value: T) : AppResult<T>()
    data class Failure(val error: DomainError) : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> value
        is Failure -> throw error.cause ?: RuntimeException(error.message)
    }

    fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    fun <R> flatMap(transform: (T) -> AppResult<R>): AppResult<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    companion object {
        fun <T> success(value: T): AppResult<T> = Success(value)
        fun failure(error: DomainError): AppResult<Nothing> = Failure(error)
    }
}

suspend fun <T> appResultOf(block: suspend () -> T): AppResult<T> = try {
    AppResult.success(block())
} catch (e: Throwable) {
    AppResult.failure(DomainError.fromThrowable(e))
}
