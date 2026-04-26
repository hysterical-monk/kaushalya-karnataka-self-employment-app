package com.kaushalya.karnataka.core.result

sealed interface UiResult<out T> {
    data object Loading : UiResult<Nothing>
    data class Success<T>(val data: T) : UiResult<T>
    data class Error(val message: String, val cause: Throwable? = null) : UiResult<Nothing>
}

inline fun <T, R> UiResult<T>.map(transform: (T) -> R): UiResult<R> = when (this) {
    is UiResult.Success -> UiResult.Success(transform(data))
    is UiResult.Error -> this
    UiResult.Loading -> UiResult.Loading
}
