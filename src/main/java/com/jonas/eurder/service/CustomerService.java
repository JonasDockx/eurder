package com.jonas.eurder.service;

import com.jonas.eurder.api.dto.CreateCustomerDTO;
import com.jonas.eurder.api.dto.CustomerDTO;
import com.jonas.eurder.api.dto.CustomerMapper;
import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerDTO createCustomer(CreateCustomerDTO dto) {
        Customer customer = customerMapper.toCustomer(dto);
        customerRepository.save(customer);
        return customerMapper.toDTO(customer);
    }
}
