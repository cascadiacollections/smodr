package com.kevintcoughlin.smodr.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class SmodrDataExceptionTest {

    @Test
    fun `SmodrHttpException carries correct code and message`() {
        val exception = SmodrHttpException(404, "/feed")
        assertEquals(404, exception.code)
        assertTrue(exception.message!!.contains("404"))
        assertTrue(exception.message!!.contains("/feed"))
    }

    @Test
    fun `SmodrEmptyBodyException message references endpoint`() {
        val exception = SmodrEmptyBodyException("/episodes")
        assertTrue(exception.message!!.contains("/episodes"))
    }

    @Test
    fun `SmodrDecodeException wraps cause and references endpoint`() {
        val cause = IllegalArgumentException("bad json")
        val exception = SmodrDecodeException("/feed.xml", cause)
        assertEquals(cause, exception.cause)
        assertTrue(exception.message!!.contains("/feed.xml"))
    }

    @Test
    fun `SmodrNetworkException wraps IOException and references endpoint`() {
        val cause = IOException("timeout")
        val exception = SmodrNetworkException("/feed", cause)
        assertEquals(cause, exception.cause)
        assertTrue(exception.message!!.contains("/feed"))
    }

    @Test
    fun `all subtypes are SmodrDataException`() {
        assertTrue(SmodrHttpException(500, "/feed") is SmodrDataException)
        assertTrue(SmodrEmptyBodyException("/feed") is SmodrDataException)
        assertTrue(SmodrDecodeException("/feed", RuntimeException()) is SmodrDataException)
        assertTrue(SmodrNetworkException("/feed", IOException()) is SmodrDataException)
    }
}
