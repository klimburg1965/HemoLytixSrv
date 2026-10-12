package de.hemolytix.server.dto;

import de.hemolytix.server.types.BloodType;
import de.hemolytix.server.types.Gender;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Gender gender,
        BloodType bloodType,
        String healthInsuranceCompany,
        String familyDoctor,
        AddressResponse address,
        int totalBloodCountsRecorded
) {}