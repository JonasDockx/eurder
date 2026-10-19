package com.jonas.eurder.api.dto;

import com.jonas.eurder.domain.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    private final AddressMapper addressMapper = new AddressMapper();

    public Customer toCustomer(CreateCustomerDTO dto) {
        return new Customer(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                addressMapper.toAddress(dto.address()),
                dto.phoneNumber()
        );
    }

    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        return new CustomerDTO(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                addressMapper.toAddressDTO(customer.getAddress()),
                customer.getPhoneNumber()
        );
    }
}
