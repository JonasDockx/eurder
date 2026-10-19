package com.jonas.eurder.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void addItem_givenValidItem_thenReturns201WithId() {
        String requestBody = """
                {
                    "name": "Book A",
                    "description": "A long book",
                    "price": 14.99,
                    "amount": 5
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/items")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Book A"))
                .body("amount", equalTo(5));
    }

    @Test
    void addItem_givenNegativePrice_thenReturns400() {
        String requestBody = """
                {
                    "name": "Book A",
                    "description": "A long book",
                    "price": -5,
                    "amount": 5
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/items")
                .then()
                .statusCode(400);
    }

    @Test
    void addItem_givenMissingAmount_thenReturns400() {
        String requestBody = """
                {
                    "name": "Book A",
                    "description": "A long book",
                    "price": 14.99
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/items")
                .then()
                .statusCode(400);
    }

}