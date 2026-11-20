package com.jonas.eurder.service;

import com.jonas.eurder.api.dto.address.AddressMapper;
import com.jonas.eurder.api.dto.customer.CreateCustomerDTO;
import com.jonas.eurder.api.dto.customer.CustomerDTO;
import com.jonas.eurder.api.dto.customer.CustomerMapper;
import com.jonas.eurder.api.dto.customer.UpdateCustomerDTO;
import com.jonas.eurder.domain.Customer;
import com.jonas.eurder.domain.Role;
import com.jonas.eurder.domain.User;
import com.jonas.eurder.repository.CustomerRepository;
import com.jonas.eurder.repository.UserRepository;
import com.jonas.eurder.service.exception.EmailAlreadyExistsException;
import com.jonas.eurder.service.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper, AddressMapper addressMapper, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.addressMapper = addressMapper;
        this.userRepository = userRepository;
    }

    public CustomerDTO createCustomer(CreateCustomerDTO dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email address already exists");
        }
        Customer customer = customerMapper.toCustomer(dto);
        customerRepository.save(customer);
        userRepository.save(new User(dto.email(), dto.password(), Role.CUSTOMER, customer.getId()));
        logger.info("Customer created with id {}", customer.getId());
        return customerMapper.toDTO(customer);
    }

    public CustomerDTO updateCustomer(String id, UpdateCustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer with id " + id + " could not be found."));
        String oldEmail = customer.getEmail();
        boolean emailChanged = !oldEmail.equalsIgnoreCase(dto.email());
        if (emailChanged && customerRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email address already exists");
        }
        customer.update(dto.firstName(), dto.lastName(), dto.email(),
                addressMapper.toAddress(dto.address()), dto.phoneNumber());
        customerRepository.save(customer);
        if (emailChanged) {
            userRepository.findByEmail(oldEmail).ifPresent(user -> {
                userRepository.delete(user);
                userRepository.save(user.withEmail(dto.email()));
            });
        }
        logger.info("Customer with id {} updated", id);
        return customerMapper.toDTO(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        logger.info("Fetching all customers");
        return customerRepository.findAll().stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(String id) {
        logger.info("Fetching customer with id {}", id);
        return customerRepository.findById(id)
                .map(customerMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("Customer with id " + id + " could not be found"));
    }
}
