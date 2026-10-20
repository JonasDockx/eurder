package com.jonas.eurder.service;

import com.jonas.eurder.api.dto.CreateCustomerDTO;
import com.jonas.eurder.api.dto.CustomerDTO;
import com.jonas.eurder.api.dto.CustomerMapper;
import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerDTO createCustomer(CreateCustomerDTO dto) {
        Customer customer = customerMapper.toCustomer(dto);
        customerRepository.save(customer);
        logger.info("Customer created with id {}", customer.getId());
        return customerMapper.toDTO(customer);
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.getAll().stream()
                .map(customerMapper::toDTO)
                .toList();
    }
}
