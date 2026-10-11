import Foundation
import XCTest
@testable import MySDK

/// Illustrative code: SDKClient, SDKError, and transport types are assumed.
final class OtpContractTests: XCTestCase {

    func testGetOtpCodeReturnsSixDigitCodeOn200() async throws {
        let http = StubHTTPTransport(
            statusCode: 200,
            body: #"{"code":"123321"}"#
        )
        let sdk = SDKClient(transport: http)

        let code = try await sdk.getOtpCode()

        XCTAssertEqual(code, "123321")
        let requestCount = await http.requestCount
        XCTAssertEqual(requestCount, 1)
    }

    func testGetOtpCodeMaps401ToUnauthorized() async {
        let http = StubHTTPTransport(statusCode: 401, body: #"{}"#)
        let sdk = SDKClient(transport: http)

        do {
            _ = try await sdk.getOtpCode()
            XCTFail("Expected public unauthorized error")
        } catch SDKError.unauthorized {
            // Expected public SDK contract.
        } catch {
            XCTFail("Expected unauthorized SDK error, got a different error type")
        }
    }

    func testGetOtpCodeMaps429ToRateLimited() async {
        let http = StubHTTPTransport(statusCode: 429, body: #"{}"#)
        let sdk = SDKClient(transport: http)

        do {
            _ = try await sdk.getOtpCode()
            XCTFail("Expected public rate-limit error")
        } catch SDKError.rateLimited {
            // Expected public SDK contract.
        } catch {
            XCTFail("Expected rate-limited SDK error, got a different error type")
        }
    }

    func testGetOtpCodeMapsTimeoutToPublicError() async {
        let http = StubHTTPTransport(error: URLError(.timedOut))
        let sdk = SDKClient(transport: http)

        do {
            _ = try await sdk.getOtpCode()
            XCTFail("Expected public timeout error")
        } catch SDKError.timeout {
            // Expected public SDK contract.
        } catch {
            XCTFail("Expected timeout SDK error, got a different error type")
        }
    }
}
