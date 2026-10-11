import Foundation
@testable import MySDK

/// Replace only the network boundary; SDK parsing/error mapping remain real.
/// Assumes MySDK declares HTTPTransport, HTTPRequest, and HTTPResponse.
actor StubHTTPTransport: HTTPTransport {
    private let result: Result<HTTPResponse, Error>
    private(set) var requestCount = 0

    init(statusCode: Int, body: String) {
        result = .success(HTTPResponse(
            statusCode: statusCode,
            body: Data(body.utf8)
        ))
    }

    init(error: Error) {
        result = .failure(error)
    }

    func send(_ request: HTTPRequest) async throws -> HTTPResponse {
        requestCount += 1
        return try result.get()
    }
}
