package com.kevintcoughlin.smodr.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository translating Retrofit2 responses into strongly typed [NetworkResult].
 */
class NetworkRepository(private val api: ApiService) {

    suspend fun fetchEpisode(id: String): NetworkResult<String> = safeApiCall { api.getEpisode(id) }

    private suspend inline fun <reified T> safeApiCall(crossinline call: suspend () -> Response<T>): NetworkResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = call()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) NetworkResult.Success(body, response.code())
                    else NetworkResult.SerializationError(NullPointerException("Response body null"))
                } else {
                    NetworkResult.HttpError(response.code(), response.message(), response.errorBody()?.string())
                }
            } catch (e: Exception) {
                NetworkResult.NetworkError(e)
            }
        }
    }
}

