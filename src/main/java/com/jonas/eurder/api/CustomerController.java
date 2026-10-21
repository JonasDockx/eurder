package com.jonas.eurder.api;

import com.jonas.eurder.api.dto.customer.CreateCustomerDTO;
import com.jonas.eurder.api.dto.customer.CustomerDTO;
import com.jonas.eurder.api.dto.eurder.OrderReportDTO;
import com.jonas.eurder.domain.Feature;
import com.jonas.eurder.domain.User;
import com.jonas.eurder.service.CustomerService;
import com.jonas.eurder.service.OrderService;
import com.jonas.eurder.service.SecurityService;
import com.jonas.eurder.service.exception.ForbiddenException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;
    private final SecurityService securityService;

    public CustomerController(CustomerService customerService, OrderService orderService, SecurityService securityService) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.securityService = securityService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO registerNewCustomer(@Valid @RequestBody CreateCustomerDTO dto) {
        return customerService.createCustomer(dto);
    }

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerDTO> getAllCustomers(@RequestHeader(value = "Authorization", required = false) String authorization) {
        securityService.validateAuthorization(authorization, Feature.VIEW_ALL_CUSTOMERS);
        return customerService.getAllCustomers();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO getCustomer(@RequestHeader(value = "Authorization", required = false) String authorization,
                                   @PathVariable String id) {
        securityService.validateAuthorization(authorization, Feature.VIEW_SINGLE_CUSTOMER);
        return customerService.getCustomer(id);
    }

    @GetMapping(path = "/{customerId}/orders", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public OrderReportDTO getOrderReport(@RequestHeader(value = "Authorization", required = false) String authorization,
                                         @PathVariable String customerId) {
        User user = securityService.validateAuthorization(authorization, Feature.VIEW_REPORT_OF_ORDERS);
        if (!customerId.equals(user.getCustomerId())) {
            throw new ForbiddenException("You can only view your own order report");
        }
        return orderService.getOrderReport(user.getCustomerId());
    }
}
