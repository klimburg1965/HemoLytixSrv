package de.hemolytix.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "Straße darf nicht leer sein.")
        String street,

        String houseNumber,

        @NotBlank(message = "PLZ darf nicht leer sein.")
        @Size(max = 10, message = "PLZ ist zu lang.")
        String zipCode,

        @NotBlank(message = "Stadt darf nicht leer sein.")
        String city
) {}