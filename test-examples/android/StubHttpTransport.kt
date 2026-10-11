package com.example.sdkcontracts

import com.example.mysdk.HttpRequest
import com.example.mysdk.HttpResponse
import com.example.mysdk.HttpTransport

/** Replaces the HTTP boundary, not the SDK method under test. */
internal class StubHttpTransport private constructor(
    private val result: Result<HttpResponse>
) : HttpTransport {
    val requests = mutableListOf<HttpRequest>()

    override suspend fun execute(request: HttpRequest): HttpResponse {
        requests += request
        return result.getOrThrow()
    }

    companion object {
        fun respond(statusCode: Int, body: String) = StubHttpTransport(
            Result.success(HttpResponse(
                statusCode = statusCode,
                body = body.toByteArray(Charsets.UTF_8)
            ))
        )

        fun fail(error: Throwable) = StubHttpTransport(Result.failure(error))
    }
}
