package com.jonas.eurder.api;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuthenticationIntegrationTest extends IntegrationTestBase {

    @ParameterizedTest
    @ValueSource(strings = {
            "Bearer some-token",    // a different scheme than Basic
            "Basic not-base64!!",   // not valid Base64
            "Basic YWRtaw4="        // Base64 of "admin": no ":password" part
    })
    void givenMalformedAuthorizationHeader_thenReturns401(String header) {
        notLoggedIn()
                .header("Authorization", header)
                .when()
                .get("/customers")
                .then()
                .statusCode(401);
    }
}
