package com.example.sdkcontracts

import com.example.mysdk.SdkClient
import com.example.mysdk.SdkErrorCode
import com.example.mysdk.SdkException
import java.net.SocketTimeoutException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertFailsWith

/** Illustrative code: adapt package, SDK types and error categories to the real SDK. */
class OtpContractTest {

    @Test
    fun `200 returns a six digit OTP`() = runTest {
        val http = StubHttpTransport.respond(
            statusCode = 200,
            body = """{"code":"123321"}"""
        )
        val sdk = SdkClient(transport = http)

        val code = sdk.getOtpCode()

        assertEquals("123321", code)
        assertEquals(1, http.requests.size)
    }

    @Test
    fun `401 maps to public unauthorized error`() = runTest {
        val sdk = SdkClient(transport = StubHttpTransport.respond(401, "{}"))

        val error = assertFailsWith<SdkException> {
            sdk.getOtpCode()
        }

        assertEquals(SdkErrorCode.UNAUTHORIZED, error.code)
    }

    @Test
    fun `429 maps to public rate limited error`() = runTest {
        val sdk = SdkClient(transport = StubHttpTransport.respond(429, "{}"))

        val error = assertFailsWith<SdkException> {
            sdk.getOtpCode()
        }

        assertEquals(SdkErrorCode.RATE_LIMITED, error.code)
    }

    @Test
    fun `transport timeout maps to public timeout error`() = runTest {
        val sdk = SdkClient(transport = StubHttpTransport.fail(
            SocketTimeoutException("simulated timeout")
        ))

        val error = assertFailsWith<SdkException> {
            sdk.getOtpCode()
        }

        assertEquals(SdkErrorCode.TIMEOUT, error.code)
    }
}
