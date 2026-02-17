package com.kevintcoughlin.smodr.network

import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

private class FakeApiService(
    var nextResponse: Response<String>,
) : ApiService {
    override suspend fun getEpisode(id: String): Response<String> = nextResponse
}

class NetworkRepositoryTest {

    @Test
    fun `fetchEpisode returns Success on 200`() = runTest {
        val repo = NetworkRepository(FakeApiService(Response.success("ok")))
        val result = repo.fetchEpisode("123")
        assertTrue(result is NetworkResult.Success)
        result as NetworkResult.Success
        assertEquals("ok", result.data)
        assertEquals(200, result.code)
    }

    @Test
    fun `fetchEpisode returns HttpError on 404`() = runTest {
        val body = "not found".toResponseBody("text/plain".toMediaType())
        val repo = NetworkRepository(FakeApiService(Response.error(404, body)))
        val result = repo.fetchEpisode("404")
        assertTrue(result is NetworkResult.HttpError)
        result as NetworkResult.HttpError
        assertEquals(404, result.code)
        assertEquals("not found", result.body)
    }

    @Test
    fun `fetchEpisode returns SerializationError when body null`() = runTest {
        val response = Response.success<String>(null)
        val repo = NetworkRepository(FakeApiService(response))
        val result = repo.fetchEpisode("null")
        assertTrue(result is NetworkResult.SerializationError)
    }
}

