package com.jonas.eurder.api;

import com.jonas.eurder.api.dto.CreateCustomerDTO;
import com.jonas.eurder.api.dto.CustomerDTO;
import com.jonas.eurder.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO registerNewCustomer(@Valid @RequestBody CreateCustomerDTO dto) {
        return customerService.createCustomer(dto);
    }
}
