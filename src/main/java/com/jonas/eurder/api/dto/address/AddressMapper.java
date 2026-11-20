package com.jonas.eurder.api.dto.address;

import com.jonas.eurder.domain.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toAddress(AddressDTO dto) {
        return new Address(
                dto.street(),
                dto.houseNumber(),
                dto.postalCode(),
                dto.city(),
                dto.country()
        );
    }

    public AddressDTO toAddressDTO(Address address) {
        return new AddressDTO(
                address.street(),
                address.houseNumber(),
                address.postalCode(),
                address.city(),
                address.country()
        );
    }
}
