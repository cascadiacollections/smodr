package com.kevintcoughlin.smodr.network

/**
 * Represents the outcome of a network call.
 */
sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T, val code: Int) : NetworkResult<T>
    data class HttpError(val code: Int, val message: String?, val body: String?) : NetworkResult<Nothing>
    data class NetworkError(val throwable: Throwable) : NetworkResult<Nothing>
    data class SerializationError(val throwable: Throwable) : NetworkResult<Nothing>
}

