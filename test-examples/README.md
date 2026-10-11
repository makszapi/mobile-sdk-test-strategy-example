# SDK automation example

**Capability:** Networking and error handling through an SDK API  
**Platforms:** iOS (Swift/XCTest) and Android (Kotlin/JUnit + coroutine tests)

## Goal and assumptions

Demonstrate how I would verify the **same consumer-visible contract** on iOS and Android using native tests, while controlling the SDK's HTTP dependency to keep feedback fast and deterministic.

For this *illustrative* example, `getOtpCode()` makes a backend request and returns a **six-digit OTP as a `String`** on success. The SDK translates HTTP/transport failures into documented public SDK errors. This example is intentionally **not a standalone runnable project**. The SDK interfaces and models are illustrative.

### Assumed SDK interfaces (not included)

#### iOS — `MySDK`

```swift
public protocol HTTPTransport {
    func send(_ request: HTTPRequest) async throws -> HTTPResponse
}

// HTTPResponse(statusCode: Int, body: Data)
// SDKClient(transport: HTTPTransport)
// SDKClient.getOtpCode() async throws -> String
// SDKError.unauthorized, .rateLimited, .timeout
```

#### Android — `com.example.mysdk`

```kotlin
interface HttpTransport {
    suspend fun execute(request: HttpRequest): HttpResponse
}

// HttpResponse(statusCode: Int, body: ByteArray)
// SdkClient(transport: HttpTransport)
// suspend fun SdkClient.getOtpCode(): String
// SdkException.code: SdkErrorCode (UNAUTHORIZED, RATE_LIMITED, TIMEOUT)
```

The injection points are test seams for illustration. If the SDK does not permit HTTP transport injection, use a custom `URLProtocol` with a test-configured `URLSession` on iOS or `MockWebServer` with the SDK pointed to a local server on Android, where supported.

## Assumed expected public contract and test scenarios

| Scenario | Controlled dependency outcome | Expected SDK behavior | Coverage |
| --- | --- | --- | --- |
| Success | HTTP 200: `{"code":"123321"}` | Returns the expected six-digit OTP | iOS + Android |
| Unauthorized request | HTTP 401 | Exposes the public `unauthorized` error | iOS + Android |
| Too many requests | HTTP 429 | Exposes a public `rateLimited` error | iOS + Android |
| Network timeout | HTTP transport throws timeout | Exposes a public `timeout` error | iOS + Android |


## Test boundary

```text
Native XCTest / JUnit test
        |
        v
Actual SDK public API: getOtpCode()
        |
        v
Actual SDK response parsing + error mapping
        |
        v
Injected fake HTTP transport  <-- controlled response / timeout
```

The tests **do not mock the SDK method being tested**. Only its external network boundary is replaced. This validates observable SDK behavior, including mapping from backend or transport failures to public SDK errors.

## Implementation notes

- Each test creates a **fresh SDK client** and **fresh fake transport** to avoid shared state.
- The successful case asserts the returned value and **format**; the failure cases assert **public SDK error categories**, not internal HTTP-library exceptions.
- iOS and Android cover the **same four scenarios** with platform-native XCTest and JUnit APIs.
- These tests validate errors at the SDK layer. They do **not** prove actual OTP delivery, server-side expiry, one-time use, retry limits, or rate-limit enforcement; those need backend/integration tests.

