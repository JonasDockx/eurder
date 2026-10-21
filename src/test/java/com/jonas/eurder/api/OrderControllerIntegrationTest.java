package com.jonas.eurder.api;

import com.jonas.eurder.domain.*;
import com.jonas.eurder.repository.CustomerRepository;
import com.jonas.eurder.repository.ItemRepository;
import com.jonas.eurder.repository.OrderRepository;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
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

    @Autowired
    private OrderRepository orderRepository;

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
                .config(RestAssuredConfig.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
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
                .config(RestAssuredConfig.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(404);
    }

    @Test
    void placeOrder_givenKnownCustomerAndNonExistentItem_thenReturns404AndStockIsUnchanged() {
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
                .config(RestAssuredConfig.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
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
                .config(RestAssuredConfig.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(400);
    }

    @Test
    void givenPlacedOrder_whenItemPriceIsUpdated_thenOrderKeepsTheOriginalPrice() {
        Customer customer = new Customer("Jonas", "Dockx", "jonas@test.com",
                new Address("Test street", "1", "1234", "Brussels"),
                "012345679");
        customerRepository.save(customer);
        Item item = new Item("Book A", "A long book", new BigDecimal("14.99"), 5);
        itemRepository.save(item);

        String originalRequestBody = """
                {
                    "customerId": "%s",
                    "itemGroups": [
                        {
                            "itemId": "%s",
                            "amount": 2
                        }
                    ]
                }
                """.formatted(customer.getId(), item.getId());

        String orderId = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(originalRequestBody)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .extract().path("id");

        String updatedRequestBody = """
                {
                    "name": "Book A",
                    "description": "A long book",
                    "price": 50.99,
                    "amount": 5
                }
                """;

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(updatedRequestBody)
                .when()
                .put("/items/" + item.getId())
                .then()
                .statusCode(200);

        assertThat(orderRepository.getById(orderId).orElseThrow().getTotalPrice()).isEqualByComparingTo("29.98");
    }

    @Test
    void reorder_givenOwnOrder_thenReturns201WithNewOrderAtCurrentPrice() {
        Customer customer = saveCustomer("Jonas");
        Item item = saveItem("14.99", 5);
        Order originalOrder = saveOrder(customer, item, 2);

        item.update("Book A", "A long book", new BigDecimal("20.00"), 5);
        itemRepository.save(item);

        given()
                .port(port)
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(reorderBody(customer.getId()))
                .when()
                .post("/orders/" + originalOrder.getId() + "/reorder")
                .then()
                .statusCode(201)
                .body("id", not(equalTo(originalOrder.getId())))
                .body("customerId", equalTo(customer.getId()))
                .body("itemGroups[0].itemId", equalTo(item.getId()))
                .body("itemGroups[0].amount", equalTo(2))
                .body("totalPrice", comparesEqualTo(new BigDecimal("40.00")));

        assertThat(orderRepository.getById(originalOrder.getId()).orElseThrow().getTotalPrice())
                .isEqualByComparingTo("29.98");
        assertThat(itemRepository.getById(item.getId()).orElseThrow().getAmount()).isEqualTo(3);
    }

    @Test
    void reorder_givenOrderOfAnotherCustomer_thenReturns403AndStockIsUnchanged() {
        Customer jonas = saveCustomer("Jonas");
        Customer bram = saveCustomer("Bram");
        Item item = saveItem("14.99", 5);
        Order ordersOfJonas = saveOrder(jonas, item, 2);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(reorderBody(bram.getId()))
                .when()
                .post("/orders/" + ordersOfJonas.getId() + "/reorder")
                .then()
                .statusCode(403);

        assertThat(itemRepository.getById(item.getId()).orElseThrow().getAmount()).isEqualTo(5);
    }

    @Test
    void reorder_givenUnknownOrder_thenReturns404() {
        Customer customer = saveCustomer("Jonas");

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(reorderBody(customer.getId()))
                .when()
                .post("/orders/does-not-exist/reorder")
                .then()
                .statusCode(404);
    }

    @Test
    void reorder_givenUnknownCustomer_thenReturns404() {
        Customer customer = saveCustomer("Jonas");
        Item item = saveItem("14.99", 5);
        Order order = saveOrder(customer, item, 2);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(reorderBody("does-not-exist"))
                .when()
                .post("/orders/" + order.getId() + "/reorder")
                .then()
                .statusCode(404);
    }

    @Test
    void reorder_givenMissingCustomerId_thenReturns400() {
        Customer customer = saveCustomer("Jonas");
        Item item = saveItem("14.99", 5);
        Order order = saveOrder(customer, item, 2);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/orders/" + order.getId() + "/reorder")
                .then()
                .statusCode(400);
    }

    @Test
    void getShippingToday_returnsTodaysItemGroupsWithShippingAddress() {
        Customer customer = saveCustomer("Jonas");
        Item itemInStock = saveItem("14.99", 5);
        Item itemOutOfStock = saveItem("1.50", 0);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Order order = new Order(customer.getId(), List.of(
                new ItemGroup(itemInStock, 2, yesterday),       // ships today
                new ItemGroup(itemOutOfStock, 1, yesterday)));  // ships in 6 days
        orderRepository.save(order);

        String orderPath = "find { it.orderId == '%s' }".formatted(order.getId());

        given()
                .port(port)
                .when()
                .get("/orders/shipping-today")
                .then()
                .statusCode(200)
                .body("orderId", hasItem(order.getId()))
                .body(orderPath + ".shippingAddress.street", equalTo("Test street"))
                .body(orderPath + ".shippingAddress.city", equalTo("Brussels"))
                .body(orderPath + ".itemGroups", hasSize(1))
                .body(orderPath + ".itemGroups[0].itemId", equalTo(itemInStock.getId()))
                .body(orderPath + ".itemGroups[0].amount", equalTo(2));
    }

    @Test
    void getShippingToday_doesNotContainOrdersShippingLater() {
        Customer customer = saveCustomer("Jonas");
        Item item = saveItem("14.99", 5);
        Order orderShippingTomorrow = saveOrder(customer, item, 1);

        given()
                .port(port)
                .when()
                .get("/orders/shipping-today")
                .then()
                .statusCode(200)
                .body("orderId", not(hasItem(orderShippingTomorrow.getId())));
    }

    private Customer saveCustomer(String firstName) {
        Customer customer = new Customer(firstName, "Test", firstName.toLowerCase() + "@test.com",
                new Address("Test street", "1", "1000", "Brussels"),
                "0123456789");
        customerRepository.save(customer);
        return customer;
    }

    private Item saveItem(String price, int stock) {
        Item item = new Item("Book A", "A long book", new BigDecimal(price), stock);
        itemRepository.save(item);
        return item;
    }

    private Order saveOrder(Customer customer, Item item, int amount) {
        Order order = new Order(customer.getId(), List.of(new ItemGroup(item, amount, LocalDate.now())));
        orderRepository.save(order);
        return order;
    }

    private String reorderBody(String customerId) {
        return """
                { "customerId": "%s" }
                """.formatted(customerId);
    }
}