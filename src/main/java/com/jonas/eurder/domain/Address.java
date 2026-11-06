package com.jonas.eurder.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Address(String street, String houseNumber, String postalCode, String city) {

}
