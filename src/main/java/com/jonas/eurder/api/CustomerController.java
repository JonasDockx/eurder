package com.jonas.eurder.api;

import com.jonas.eurder.api.dto.customer.CreateCustomerDTO;
import com.jonas.eurder.api.dto.customer.CustomerDTO;
import com.jonas.eurder.api.dto.eurder.OrderReportDTO;
import com.jonas.eurder.service.CustomerService;
import com.jonas.eurder.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    public CustomerController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO registerNewCustomer(@Valid @RequestBody CreateCustomerDTO dto) {
        return customerService.createCustomer(dto);
    }

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public CustomerDTO getCustomer(@PathVariable String id) {
        return customerService.getCustomer(id);
    }

    @GetMapping(path = "/{customerId}/orders", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public OrderReportDTO getOrderReport(@PathVariable String customerId) {
        return orderService.getOrderReport(customerId);
    }
}
