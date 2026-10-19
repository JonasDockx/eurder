package com.jonas.eurder.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void createCustomer_givenValidCustomer_thenReturns201WithId() {
        String requestBody = """
                {
                    "firstName": "Jonas",
                    "lastName": "Dockx",
                    "email": "jonas@test.com",
                    "address": { "street": "Test street", "houseNumber": "1", "postalCode": "1000", "city": "Brussels" },
                    "phoneNumber": "0123456789"
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("Jonas"));
    }
}