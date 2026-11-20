package com.jonas.eurder.api;

import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.domain.Item;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

class FrontendSupportIntegrationTest extends IntegrationTestBase {

    @Test
    void getAllItems_withoutLoggingIn_thenReturnsItemsWithUrgency() {
        Item item = saveItem("Public item", 3);

        notLoggedIn()
                .when()
                .get("/items")
                .then()
                .statusCode(200)
                .body("find { it.id == '%s' }.name".formatted(item.getId()), equalTo("Public item"))
                .body("find { it.id == '%s' }.urgencyLevel".formatted(item.getId()), equalTo("STOCK_LOW"));
    }

    @Test
    void getItem_givenExistingId_thenReturnsItem() {
        Item item = saveItem("Single item", 12);

        notLoggedIn()
                .when()
                .get("/items/" + item.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(item.getId()))
                .body("description", equalTo("Test item"))
                .body("amount", equalTo(12))
                .body("urgencyLevel", equalTo("STOCK_HIGH"));
    }

    @Test
    void getItem_givenUnknownId_thenReturns404() {
        notLoggedIn()
                .when()
                .get("/items/does-not-exist")
                .then()
                .statusCode(404);
    }

    private String updateBody(String email) {
        return """
                {
                    "firstName": "Updated",
                    "lastName": "Name",
                    "email": "%s",
                    "address": { "street": "New street", "houseNumber": "99", "postalCode": "75001", "city": "Paris", "country": "France" },
                    "phoneNumber": "0499999999"
                }
                """.formatted(email);
    }

    @Test
    void updateCustomer_asAdmin_thenReturns200AndCustomerIsUpdated() {
        Customer customer = saveCustomer("jan");

        loggedInAsAdmin()
                .contentType(ContentType.JSON)
                .body(updateBody(customer.getEmail()))
                .when()
                .put("/customers/" + customer.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(customer.getId()))
                .body("firstName", equalTo("Updated"))
                .body("address.city", equalTo("Paris"))
                .body("address.country", equalTo("France"));

        Customer updated = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(updated.getLastName()).isEqualTo("Name");
        assertThat(updated.getAddress().country()).isEqualTo("France");
    }

    @Test
    void updateCustomer_givenNewEmail_thenCustomerCanLogInWithNewEmail() {
        Customer customer = saveCustomer("piet");
        String newEmail = uniqueEmail("piet-new");

        loggedInAsAdmin()
                .contentType(ContentType.JSON)
                .body(updateBody(newEmail))
                .when()
                .put("/customers/" + customer.getId())
                .then()
                .statusCode(200)
                .body("email", equalTo(newEmail));

        notLoggedIn()
                .auth().preemptive().basic(newEmail, PASSWORD)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("customerId", equalTo(customer.getId()));
    }

    @Test
    void updateCustomer_givenEmailOfAnotherCustomer_thenReturns409() {
        Customer customer = saveCustomer("an");
        Customer other = saveCustomer("other");

        loggedInAsAdmin()
                .contentType(ContentType.JSON)
                .body(updateBody(other.getEmail()))
                .when()
                .put("/customers/" + customer.getId())
                .then()
                .statusCode(409);
    }

    @Test
    void updateCustomer_givenUnknownId_thenReturns404() {
        loggedInAsAdmin()
                .contentType(ContentType.JSON)
                .body(updateBody(uniqueEmail("nobody")))
                .when()
                .put("/customers/does-not-exist")
                .then()
                .statusCode(404);
    }

    @Test
    void updateCustomer_givenInvalidEmail_thenReturns400() {
        Customer customer = saveCustomer("bad");

        loggedInAsAdmin()
                .contentType(ContentType.JSON)
                .body(updateBody("not-an-email"))
                .when()
                .put("/customers/" + customer.getId())
                .then()
                .statusCode(400);
    }

    @Test
    void updateCustomer_asCustomer_thenReturns403() {
        Customer customer = saveCustomer("sneaky");

        loggedInAs(customer)
                .contentType(ContentType.JSON)
                .body(updateBody(customer.getEmail()))
                .when()
                .put("/customers/" + customer.getId())
                .then()
                .statusCode(403);
    }

    @Test
    void me_asAdmin_thenReturnsAdminRole() {
        loggedInAsAdmin()
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(ADMIN_EMAIL))
                .body("role", equalTo("ADMIN"))
                .body("customerId", nullValue());
    }

    @Test
    void me_asCustomer_thenReturnsCustomerRoleAndId() {
        Customer customer = saveCustomer("me");

        loggedInAs(customer)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("role", equalTo("CUSTOMER"))
                .body("customerId", equalTo(customer.getId()));
    }

    @Test
    void me_givenWrongPassword_thenReturns401() {
        notLoggedIn()
                .auth().preemptive().basic(ADMIN_EMAIL, "wrong")
                .when()
                .get("/users/me")
                .then()
                .statusCode(401);
    }

    @Test
    void preflightFromAngularDevServer_isAllowed() {
        notLoggedIn()
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "PUT")
                .when()
                .options("/items/some-id")
                .then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", equalTo("http://localhost:4200"));
    }

    private Item saveItem(String name, int stock) {
        Item item = new Item(name, "Test item", new BigDecimal("9.99"), stock);
        itemRepository.save(item);
        return item;
    }
}