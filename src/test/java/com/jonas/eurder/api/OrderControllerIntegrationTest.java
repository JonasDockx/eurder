package com.jonas.eurder.api;

import com.jonas.eurder.domain.Address;
import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.domain.Item;
import com.jonas.eurder.repository.CustomerRepository;
import com.jonas.eurder.repository.ItemRepository;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void placeOrder_givenKnownCustomerAndItemInStock_thenReturns201WithTotalAndShippingDate() {
        Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com",
                new Address("Test street", "1", "1234", "Brussels"),
                "012345679");
        customerRepository.save(customer);
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        itemRepository.save(item);

        String requestBody = """
                {
                    "customerId": "%s",
                    "itemGroups": [
                        { "itemId": "%s", "amount": 2 }
                    ]
                }
                """.formatted(customer.getId(), item.getId());

        given()
                .port(port)
                .config(RestAssuredConfig.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("totalPrice", comparesEqualTo(new BigDecimal("29.98")))
                .body("itemGroups[0].shippingDate", equalTo(LocalDate.now().plusDays(1).toString()));

        assertThat(itemRepository.getById(item.getId()).orElseThrow().getAmount()).isEqualTo(3);
    }

    @Test
    void placeOrder_givenUnknownCustomerId_thenReturns404() {
        Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com",
                new Address("Test street", "1", "1234", "Brussels"),
                "012345679");
        customerRepository.save(customer);
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        itemRepository.save(item);

        String requestBody = """
                {
                    "customerId": "1234",
                    "itemGroups": [
                        { "itemId": "%s", "amount": 2 }
                    ]
                }
                """.formatted(item.getId());

        given()
                .port(port)
                .config(RestAssuredConfig.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(404);
    }

    @Test
    void placeOrder_givenKnownCustomerAndNonExistentItem_thenReturns404WithTotalAndShippingDate() {
        Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com",
                new Address("Test street", "1", "1234", "Brussels"),
                "012345679");
        customerRepository.save(customer);
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        Item otherItem = new Item("Pen", "A blue pen", new BigDecimal("1.49"), 10);
        itemRepository.save(item);
        itemRepository.save(otherItem);

        String requestBody = """
                {
                    "customerId": "%s",
                    "itemGroups": [
                        { "itemId": "%s", "amount": 2 },
                        { "itemId": "123", "amount": 5 }
                    ]
                }
                """.formatted(customer.getId(), item.getId());

        given()
                .port(port)
                .config(RestAssuredConfig.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(404);

        assertThat(itemRepository.getById(item.getId()).orElseThrow().getAmount()).isEqualTo(5);
    }

    @Test
    void placeOrder_givenEmptyItemGroupsList_thenReturns400() {
        Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com",
                new Address("Test street", "1", "1234", "Brussels"),
                "012345679");
        customerRepository.save(customer);
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        itemRepository.save(item);

        String requestBody = """
                {
                    "customerId": "%s",
                    "itemGroups": [
                    ]
                }
                """.formatted(customer.getId());

        given()
                .port(port)
                .config(RestAssuredConfig.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(400);
    }
}