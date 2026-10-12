package de.hemolytix.server.dto;

public record AddressResponse(
        Long id,
        String street,
        String houseNumber,
        String zipCode,
        String city
) {}