package com.jonas.eurder.api;

import com.jonas.eurder.domain.Item;
import com.jonas.eurder.repository.ItemRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ItemRepository itemRepository;

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

    @Test
    void updateItem_givenValidData_thenReturns200AndItemIsUpdated() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        itemRepository.save(item);

        String requestBody = """
                {
                    "name": "Book B",
                    "description": "A short book",
                    "price": 9.99,
                    "amount": 10
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/items/" + item.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(item.getId()))
                .body("name", equalTo("Book B"))
                .body("amount", equalTo(10));

        assertThat(itemRepository.getById(item.getId()).orElseThrow().getPrice()).isEqualByComparingTo("9.99");
    }

    @Test
    void updateItem_givenUnknownId_thenReturns404() {
        String requestBody = """
                {
                    "name": "Book B",
                    "description": "A short book",
                    "price": 9.99,
                    "amount": 10
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/items/non-existing-item")
                .then()
                .statusCode(404);
    }

    @Test
    void updateItem_givenMissingAmount_thenReturns400() {
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        itemRepository.save(item);

        String requestBody = """
                {
                    "name": "Book B",
                    "description": "A short book",
                    "price": 9.99
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/items/" + item.getId())
                .then()
                .statusCode(400);
    }

}