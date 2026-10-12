package de.hemolytix.server.mapper;

import de.hemolytix.server.dto.AddressRequest;
import de.hemolytix.server.dto.AddressResponse;
import de.hemolytix.server.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    // Request -> Entity
    public Address toEntity(AddressRequest request) {
        if (request == null) return null;

        Address address = new Address();
        address.setStreet(request.street());
        address.setHouseNumber(request.houseNumber());
        address.setZipCode(request.zipCode());
        address.setCity(request.city());
        return address;
    }

    // Aktualisiert eine bestehende Address-Entity aus einem Request
    public void updateEntityFromRequest(AddressRequest request, Address address) {
        if (request == null || address == null) return;

        address.setStreet(request.street());
        address.setHouseNumber(request.houseNumber());
        address.setZipCode(request.zipCode());
        address.setCity(request.city());
    }

    // Entity -> Response
    public AddressResponse toResponse(Address address) {
        if (address == null) return null;

        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getHouseNumber(),
                address.getZipCode(),
                address.getCity()
        );
    }
}