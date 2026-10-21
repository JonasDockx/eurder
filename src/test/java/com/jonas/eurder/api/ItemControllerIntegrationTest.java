package com.jonas.eurder.api;

import com.jonas.eurder.domain.Item;
import com.jonas.eurder.domain.UrgencyLevel;
import com.jonas.eurder.repository.ItemRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

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

    @Test
    void getListOfItemsWithStockLevel_givenNoFilter_thenReturnsAllItemsWithStockLevels() {
        Item mediumItem = new Item("Book A", "A long book", new BigDecimal("14.99"), 6);
        itemRepository.save(mediumItem);
        Item highItem = new Item("Pen", "A blue pen", new BigDecimal("1.50"), 20);
        itemRepository.save(highItem);
        Item lowItem = new Item("Book B", "A short book", new BigDecimal("9.99"), 1);
        itemRepository.save(lowItem);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .when()
                .get("/items/stock")
                .then()
                .statusCode(200);
    }

    @Test
    void getStock_returnsEachItemWithItsUrgencyLevel() {
        Item lowItem = saveItemWithStock("Low", 2);
        Item mediumItem = saveItemWithStock("Medium", 7);
        Item highItem = saveItemWithStock("High", 15);

        given()
                .port(port)
                .when()
                .get("/items/stock")
                .then()
                .statusCode(200)
                .body("find { it.id == '%s' }.urgencyLevel".formatted(lowItem.getId()), equalTo("STOCK_LOW"))
                .body("find { it.id == '%s' }.urgencyLevel".formatted(mediumItem.getId()), equalTo("STOCK_MEDIUM"))
                .body("find { it.id == '%s' }.urgencyLevel".formatted(highItem.getId()), equalTo("STOCK_HIGH"));
    }

    @Test
    void getStock_returnsItemsSortedFromMostToLeastUrgent() {
        saveItemWithStock("High", 15);
        saveItemWithStock("Low", 2);
        saveItemWithStock("Medium", 7);

        List<UrgencyLevel> levels = given()
                .port(port)
                .when()
                .get("/items/stock")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("urgencyLevel", String.class)
                .stream()
                .map(UrgencyLevel::valueOf)
                .toList();

        assertThat(levels).contains(UrgencyLevel.STOCK_LOW, UrgencyLevel.STOCK_MEDIUM, UrgencyLevel.STOCK_HIGH);
        assertThat(levels).isSorted();
    }


    @Test
    void getStockByUrgencyLevel_returnsOnlyItemsWithThatLevel() {
        Item lowItem = saveItemWithStock("Low", 2);
        Item highItem = saveItemWithStock("High", 15);

        given()
                .port(port)
                .when()
                .get("/items/stock?urgencyLevel=STOCK_LOW")
                .then()
                .statusCode(200)
                .body("urgencyLevel", everyItem(equalTo("STOCK_LOW")))
                .body("id", hasItem(lowItem.getId()))
                .body("id", not(hasItem(highItem.getId())));
    }

    @Test
    void getStockByUrgencyLevel_givenUnknownLevel_thenReturns400() {
        given()
                .port(port)
                .when()
                .get("/items/stock?urgencyLevel=BANANA")
                .then()
                .statusCode(400);
    }

    private Item saveItemWithStock(String name, int stock) {
        Item item = new Item(name, "Test item", new BigDecimal("9.99"), stock);
        itemRepository.save(item);
        return item;
    }
}