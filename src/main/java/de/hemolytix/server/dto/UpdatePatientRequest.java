package de.hemolytix.server.dto;

import de.hemolytix.server.types.BloodType;
import de.hemolytix.server.types.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdatePatientRequest(

        @NotBlank(message = "Vorname darf nicht leer sein.")
        @Size(max = 50, message = "Vorname darf maximal 50 Zeichen lang sein.")
        String firstName,

        @NotBlank(message = "Nachname darf nicht leer sein.")
        @Size(max = 50, message = "Nachname darf maximal 50 Zeichen lang sein.")
        String lastName,

        @NotNull(message = "Geburtsdatum ist erforderlich.")
        @Past(message = "Geburtsdatum muss in der Vergangenheit liegen.")
        LocalDate birthDate,

        @NotNull(message = "Geschlecht muss angegeben werden.")
        Gender gender,

        BloodType bloodType,

        String healthInsuranceCompany,

        String familyDoctor,

        @Valid
        AddressRequest address
) {}