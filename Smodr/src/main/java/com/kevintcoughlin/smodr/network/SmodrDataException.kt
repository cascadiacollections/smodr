package com.kevintcoughlin.smodr.network

import java.io.IOException

/**
 * Base sealed exception hierarchy for typed network error handling in Smodr.
 *
 * Callers can use `when` expressions over the subclasses to produce
 * user-friendly messages without stringly-typed `cause` inspection.
 */
sealed class SmodrDataException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

/** Non-2xx HTTP response received for [endpoint]. */
class SmodrHttpException(
    val code: Int,
    endpoint: String,
) : SmodrDataException("HTTP $code while requesting $endpoint")

/** The server returned a 2xx response but with an empty body for [endpoint]. */
class SmodrEmptyBodyException(
    endpoint: String,
) : SmodrDataException("Empty response body from $endpoint")

/** The response body from [endpoint] could not be decoded. */
class SmodrDecodeException(
    endpoint: String,
    cause: Throwable,
) : SmodrDataException("Failed to decode response from $endpoint", cause)

/** A low-level I/O or connectivity failure occurred when requesting [endpoint]. */
class SmodrNetworkException(
    endpoint: String,
    cause: IOException,
) : SmodrDataException("Network request failed for $endpoint", cause)
