package com.kevintcoughlin.smodr.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Example Retrofit2 service definition. Replace with real endpoints when migrating.
 */
interface ApiService {
    @GET("episodes/{id}")
    suspend fun getEpisode(@Path("id") id: String): Response<String>
}

