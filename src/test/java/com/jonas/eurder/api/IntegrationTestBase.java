package com.jonas.eurder.api;

import com.jonas.eurder.domain.Address;
import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.domain.Role;
import com.jonas.eurder.domain.User;
import com.jonas.eurder.repository.CustomerRepository;
import com.jonas.eurder.repository.ItemRepository;
import com.jonas.eurder.repository.OrderRepository;
import com.jonas.eurder.repository.UserRepository;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {
    protected static final String PASSWORD = "password123";
    protected static final String ADMIN_EMAIL = "admin@eurder.com";
    protected static final String ADMIN_PASSWORD = "admin";

    @LocalServerPort
    protected int port;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected UserRepository userRepository;

    protected Customer saveCustomer(String firstName) {
        String email = uniqueEmail(firstName);
        Customer customer = new Customer(firstName, "Test", email,
                new Address("Test street", "1", "1000", "Brussels"),
                "0123456789");
        customerRepository.save(customer);
        userRepository.save(new User(email, IntegrationTestBase.PASSWORD, Role.CUSTOMER, customer.getId()));
        return customer;
    }

    protected String uniqueEmail(String firstName) {
        return firstName.toLowerCase() + "-" + UUID.randomUUID() + "@test.com";
    }

    protected RequestSpecification loggedInAs(Customer customer) {
        return given().port(port).auth().preemptive().basic(customer.getEmail(), IntegrationTestBase.PASSWORD);
    }

    protected RequestSpecification loggedInAsAdmin() {
        return given().port(port).auth().preemptive().basic(IntegrationTestBase.ADMIN_EMAIL, IntegrationTestBase.ADMIN_PASSWORD);
    }

    protected RequestSpecification notLoggedIn() {
        return given().port(port);
    }
}
