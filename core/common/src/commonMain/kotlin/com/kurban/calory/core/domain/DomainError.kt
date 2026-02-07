package com.kurban.calory.core.domain

sealed class DomainError : Exception() {
    abstract val originalMessage: String

    override val message: String get() = originalMessage

    data class NetworkError(
        override val originalMessage: String,
        override val cause: Throwable? = null
    ) : DomainError()

    data class DatabaseError(
        override val originalMessage: String,
        override val cause: Throwable? = null
    ) : DomainError()

    data class ValidationError(
        override val originalMessage: String,
        override val cause: Throwable? = null
    ) : DomainError()

    data class UnknownError(
        override val originalMessage: String,
        override val cause: Throwable? = null
    ) : DomainError()

    data class NotFound(
        override val originalMessage: String,
        override val cause: Throwable? = null
    ): DomainError()

    companion object {
        fun fromThrowable(throwable: Throwable): DomainError = when (throwable) {
            is DomainError -> throwable
            is kotlin.coroutines.cancellation.CancellationException -> UnknownError(
                originalMessage = throwable.message ?: "Unknown error",
                cause = throwable
            )
            else -> {
                val className = throwable::class.simpleName ?: ""
                when {
                    className.contains("UnknownHostException") ||
                    className.contains("SocketTimeoutException") ||
                    className.contains("IOException") ||
                    className.contains("ConnectException") -> NetworkError(
                        originalMessage = throwable.message ?: "Network error",
                        cause = throwable
                    )
                    className.contains("SQLException") -> DatabaseError(
                        originalMessage = throwable.message ?: "Database error",
                        cause = throwable
                    )
                    else -> UnknownError(
                        originalMessage = throwable.message ?: "Unknown error",
                        cause = throwable
                    )
                }
            }
        }
    }
}
