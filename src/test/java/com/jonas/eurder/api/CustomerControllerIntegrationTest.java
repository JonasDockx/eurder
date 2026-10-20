package com.jonas.eurder.api;

import com.jonas.eurder.domain.*;
import com.jonas.eurder.repository.CustomerRepository;
import com.jonas.eurder.repository.ItemRepository;
import com.jonas.eurder.repository.OrderRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    @Test
    void createCustomerWithInvalidEmail_thenShouldExpect400Error() {
        String requestBody = """
                {
                    "firstName": "Jonas",
                    "lastName": "Dockx",
                    "email": "jonas",
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
                .statusCode(400);
    }

    @Test
    void getAllCustomers_givenSavedCustomer_thenReturns200AndContainsThatCustomer() {
        Customer customer = new Customer("Pol", "De Mol", "pol@test.com",
                new Address("Test street", "2", "4321", "Brussels"),
                "0000000001");
        customerRepository.save(customer);

        given()
                .port(port)
                .when()
                .get("/customers")
                .then()
                .statusCode(200)
                .body("id", hasItem(customer.getId()))
                .body("find { it.id == '%s' }.firstName".formatted(customer.getId()), equalTo("Pol"));
    }

    @Test
    void getCustomer_givenSavedCustomer_thenReturns200AndContainsThatCustomer() {
        Customer customer = new Customer("Vital", "De Kwal", "vital@test.com",
                new Address("Test street", "3", "9999", "Brussels"),
                "0000000002");
        customerRepository.save(customer);

        given()
                .port(port)
                .when()
                .get("/customers/" + customer.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(customer.getId()))
                .body("firstName", equalTo("Vital"));

    }

    @Test
    void getCustomer_givenNonExistentId_thenReturns404() {
        Customer customer = new Customer("Marcel", "De Veldmuis", "marcel@test.com",
                new Address("Test street", "4", "1001", "Brussels"),
                "0000000003");
        customerRepository.save(customer);

        given()
                .port(port)
                .when()
                .get("/customers/123")
                .then()
                .statusCode(404);
    }

    @Test
    void getOrderReport_givenCustomerWithOrders_thenReturnsAllOrdersWithTotals() {
        Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com",
                new Address("Test street", "1", "1000", "Brussels"),
                "0123456789");
        customerRepository.save(customer);
        Item book = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        Item pen = new Item("Pen", "A blue pen", new BigDecimal("1.50"), 10);
        itemRepository.save(book);
        itemRepository.save(pen);
        Order firstOrder = new Order(customer.getId(), List.of(
                new ItemGroup(book, 2, LocalDate.now()),
                new ItemGroup(pen, 4, LocalDate.now())));
        Order secondOrder = new Order(customer.getId(), List.of(
                new ItemGroup(book, 1, LocalDate.now())));
        orderRepository.save(firstOrder);
        orderRepository.save(secondOrder);

        String firstOrderPath = "orders.find { it.orderId == '%s' }".formatted(firstOrder.getId());

        given()
                .port(port)
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)))
                .when()
                .get("/customers/" + customer.getId() + "/orders")
                .then()
                .statusCode(200)
                .body("orders", hasSize(2))
                .body("totalPrice", comparesEqualTo(new BigDecimal("50.97")))
                .body(firstOrderPath + ".totalPrice", comparesEqualTo(new BigDecimal("35.98")))
                .body(firstOrderPath + ".itemGroups", hasSize(2))
                .body(firstOrderPath + ".itemGroups.find { it.itemName == 'Pen' }.amount", equalTo(4))
                .body(firstOrderPath + ".itemGroups.find { it.itemName == 'Pen' }.totalPrice", comparesEqualTo(new BigDecimal("6.00")));
    }

    @Test
    void getOrderReport_givenCustomerWithoutOrders_thenReturns200WithEmptyList() {
        Customer customer = new Customer("Test", "Dockx", "test@test.com",
                new Address("Test street", "4", "1001", "Brussels"),
                "0000000003");
        customerRepository.save(customer);

        given()
                .port(port)
                .when()
                .get("/customers/" + customer.getId() + "/orders")
                .then()
                .statusCode(200)
                .body("orders", empty());
    }

    @Test
    void getOrderReport_givenUnknownCustomer_thenReturns404() {
        given()
                .port(port)
                .when()
                .get("/customers/non-existing-customer/orders")
                .then()
                .statusCode(404);
    }
}