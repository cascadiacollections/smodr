package com.kevintcoughlin.smodr.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Provides a lazily-initialized Retrofit2 & OkHttp3 client.
 * Adjust BASE_URL and interceptors as real endpoints are integrated.
 */
object RetrofitClient {
    private const val BASE_URL = "https://example.com/api/" // TODO replace with real base URL

    /**
     * Accept header value for RSS/podcast feed requests.
     *
     * Listing `application/rss+xml` first signals preference for RSS 2.0;
     * fallbacks cover Atom and generic XML. The wildcard `*/*` ensures servers
     * that ignore `Accept` still respond rather than returning 406.
     */
    internal const val ACCEPT_HEADER = "application/rss+xml, application/atom+xml, application/xml, text/xml, */*"

    private val loggingInterceptor: Interceptor by lazy {
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    }

    /**
     * Interceptor that sets the RSS/podcast feed `Accept` header when the
     * caller has not already specified one.
     *
     * Using a conditional check instead of always overwriting allows call-sites
     * to specify a different `Accept` value for non-feed endpoints (e.g. image
     * downloads) without interference from this interceptor.
     */
    private val acceptHeaderInterceptor: Interceptor = Interceptor { chain ->
        val request = chain.request()
        val newRequest = if (request.header("Accept") == null) {
            request.newBuilder().header("Accept", ACCEPT_HEADER).build()
        } else {
            request
        }
        chain.proceed(newRequest)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(acceptHeaderInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

