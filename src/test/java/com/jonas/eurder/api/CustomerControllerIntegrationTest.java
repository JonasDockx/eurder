package com.jonas.eurder.api;

import com.jonas.eurder.domain.Address;
import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.repository.CustomerRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerRepository customerRepository;

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
                // For the line below here, I had to use Claude. Apparently RestAssured's query language is GPath, something I was unfamiliar with
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
}