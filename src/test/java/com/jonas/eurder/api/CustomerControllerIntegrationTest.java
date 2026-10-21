package com.jonas.eurder.api;

import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.domain.Item;
import com.jonas.eurder.domain.ItemGroup;
import com.jonas.eurder.domain.Order;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

class CustomerControllerIntegrationTest extends IntegrationTestBase {

    private String registrationBody(String email) {
        return """
                {
                    "firstName": "Jonas",
                    "lastName": "Dockx",
                    "email": "%s",
                    "password": "%s",
                    "address": { "street": "Test street", "houseNumber": "1", "postalCode": "1000", "city": "Brussels" },
                    "phoneNumber": "0123456789"
                }
                """.formatted(email, PASSWORD);
    }

    @Test
    void createCustomer_givenValidCustomer_thenReturns201WithId() {
        notLoggedIn()
                .contentType(ContentType.JSON)
                .body(registrationBody(uniqueEmail("jonas")))
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("firstName", equalTo("Jonas"));
    }

    @Test
    void createCustomer_thenTheNewCustomerCanLogIn() {
        String email = uniqueEmail("jonas");

        String customerId = notLoggedIn()
                .contentType(ContentType.JSON)
                .body(registrationBody(email))
                .when()
                .post("/customers")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .port(port)
                .auth().preemptive().basic(email, PASSWORD)
                .when()
                .get("/customers/" + customerId + "/orders")
                .then()
                .statusCode(200);
    }

    @Test
    void createCustomer_givenInvalidEmail_thenReturns400() {
        notLoggedIn()
                .contentType(ContentType.JSON)
                .body(registrationBody("jonas"))
                .when()
                .post("/customers")
                .then()
                .statusCode(400);
    }

    @Test
    void getAllCustomers_givenAdmin_thenReturns200AndContainsThatCustomer() {
        Customer customer = saveCustomer("Pol");

        loggedInAsAdmin()
                .when()
                .get("/customers")
                .then()
                .statusCode(200)
                .body("id", hasItem(customer.getId()))
                .body("find { it.id == '%s' }.firstName".formatted(customer.getId()), equalTo("Pol"));
    }

    @Test
    void getAllCustomers_givenCustomer_thenReturns403() {
        Customer customer = saveCustomer("Pol");

        loggedInAs(customer)
                .when()
                .get("/customers")
                .then()
                .statusCode(403);
    }

    @Test
    void getAllCustomers_givenNoCredentials_thenReturns401() {
        notLoggedIn()
                .when()
                .get("/customers")
                .then()
                .statusCode(401);
    }

    @Test
    void getCustomer_givenAdmin_thenReturns200WithThatCustomer() {
        Customer customer = saveCustomer("Vital");

        loggedInAsAdmin()
                .when()
                .get("/customers/" + customer.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(customer.getId()))
                .body("firstName", equalTo("Vital"));
    }

    @Test
    void getCustomer_givenUnknownId_thenReturns404() {
        loggedInAsAdmin()
                .when()
                .get("/customers/does-not-exist")
                .then()
                .statusCode(404);
    }

    @Test
    void getOrderReport_givenOwnReport_thenReturnsAllOrdersWithTotals() {
        Customer customer = saveCustomer("Jonas");
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

        loggedInAs(customer)
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when()
                .get("/customers/" + customer.getId() + "/orders")
                .then()
                .statusCode(200)
                .body("orders", hasSize(2))
                .body("totalPrice", comparesEqualTo(new BigDecimal("50.97")))
                .body(firstOrderPath + ".totalPrice", comparesEqualTo(new BigDecimal("35.98")))
                .body(firstOrderPath + ".itemGroups", hasSize(2))
                .body(firstOrderPath + ".itemGroups.find { it.itemName == 'Pen' }.amount", equalTo(4))
                .body(firstOrderPath + ".itemGroups.find { it.itemName == 'Pen' }.totalPrice",
                        comparesEqualTo(new BigDecimal("6.00")));
    }

    @Test
    void getOrderReport_givenCustomerWithoutOrders_thenReturns200WithEmptyList() {
        Customer customer = saveCustomer("Anna");

        loggedInAs(customer)
                .when()
                .get("/customers/" + customer.getId() + "/orders")
                .then()
                .statusCode(200)
                .body("orders", empty());
    }

    @Test
    void getOrderReport_givenReportOfAnotherCustomer_thenReturns403() {
        Customer jonas = saveCustomer("Jonas");
        Customer bram = saveCustomer("Bram");

        loggedInAs(bram)
                .when()
                .get("/customers/" + jonas.getId() + "/orders")
                .then()
                .statusCode(403);
    }

    @Test
    void getOrderReport_givenNoCredentials_thenReturns401() {
        Customer customer = saveCustomer("Jonas");

        notLoggedIn()
                .when()
                .get("/customers/" + customer.getId() + "/orders")
                .then()
                .statusCode(401);
    }
}