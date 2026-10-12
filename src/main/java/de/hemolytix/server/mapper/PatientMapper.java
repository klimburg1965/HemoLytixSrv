package de.hemolytix.server.mapper;

import de.hemolytix.server.dto.*;
import de.hemolytix.server.model.Address;
import de.hemolytix.server.model.Patient;
import de.hemolytix.server.types.BloodType;
import de.hemolytix.server.types.Gender;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PatientMapper {

    public Patient toEntity(CreatePatientRequest request) {
        if (request == null) return null;

        Patient patient = new Patient();
        mapRequestData(request.firstName(), request.lastName(), request.birthDate(),
                request.gender(), request.bloodType(), request.healthInsuranceCompany(),
                request.familyDoctor(), request.address(), patient);
        return patient;
    }

    public void updateEntityFromRequest(UpdatePatientRequest request, Patient patient) {
        if (request == null || patient == null) return;

        mapRequestData(request.firstName(), request.lastName(), request.birthDate(),
                request.gender(), request.bloodType(), request.healthInsuranceCompany(),
                request.familyDoctor(), request.address(), patient);
    }

    private void mapRequestData(String firstName, String lastName, LocalDate birthDate,
                                Gender gender, BloodType bloodType, String insurance,
                                String doctor, AddressRequest addressRequest, Patient patient) {
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setDateOfBirth(birthDate);
        patient.setGender(gender);
        patient.setBloodType(bloodType);
        patient.setHealthInsuranceCompany(insurance);
        patient.setFamilyDoctor(doctor);

        if (addressRequest != null) {
            Address address = patient.getAddress();
            if (address == null) {
                address = new Address();
            }
            address.setStreet(addressRequest.street());
            address.setHouseNumber(addressRequest.houseNumber());
            address.setZipCode(addressRequest.zipCode());
            address.setCity(addressRequest.city());
            patient.setAddress(address);
        } else {
            patient.setAddress(null);
        }
    }

    public PatientResponse toResponse(Patient patient) {
        if (patient == null) return null;

        int totalCounts = patient.getCompleteBloodCounts() != null ? patient.getCompleteBloodCounts().size() : 0;

        AddressResponse addressResponse = null;
        if (patient.getAddress() != null) {
            Address a = patient.getAddress();
            addressResponse = new AddressResponse(a.getId(), a.getStreet(), a.getHouseNumber(), a.getZipCode(), a.getCity());
        }

        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender() != null ? patient.getGender() : Gender.UNSPECIFIED,
                patient.getBloodType(),
                patient.getHealthInsuranceCompany(),
                patient.getFamilyDoctor(),
                addressResponse,
                totalCounts
        );
    }
}