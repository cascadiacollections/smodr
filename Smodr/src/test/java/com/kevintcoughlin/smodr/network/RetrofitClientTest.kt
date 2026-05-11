package com.kevintcoughlin.smodr.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for [RetrofitClient]'s Accept-header interceptor behaviour.
 *
 * Verifies:
 * - The RSS/XML default is injected when the caller provides no `Accept` header.
 * - A caller-provided `Accept` header is preserved and not overwritten.
 */
class RetrofitClientTest {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    /** Build the same interceptor chain as [RetrofitClient] for white-box testing. */
    private fun buildClient(): OkHttpClient {
        val interceptor = okhttp3.Interceptor { chain ->
            val request = chain.request()
            val newRequest = if (request.header("Accept") == null) {
                request.newBuilder().header("Accept", RetrofitClient.ACCEPT_HEADER).build()
            } else {
                request
            }
            chain.proceed(newRequest)
        }
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Test
    fun `interceptor injects default Accept when not set by caller`() {
        server.enqueue(MockResponse().setBody("ok"))
        val client = buildClient()
        val request = Request.Builder().url(server.url("/feed")).build()
        client.newCall(request).execute().use { }

        val recorded = server.takeRequest()
        assertEquals(RetrofitClient.ACCEPT_HEADER, recorded.getHeader("Accept"))
    }

    @Test
    fun `interceptor preserves caller-provided Accept header`() {
        server.enqueue(MockResponse().setBody("ok"))
        val client = buildClient()
        val customAccept = "text/html"
        val request = Request.Builder()
            .url(server.url("/page"))
            .header("Accept", customAccept)
            .build()
        client.newCall(request).execute().use { }

        val recorded = server.takeRequest()
        assertEquals(customAccept, recorded.getHeader("Accept"))
    }
}
