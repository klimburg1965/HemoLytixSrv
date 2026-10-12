package de.hemolytix.server.service;

import de.hemolytix.server.dto.BloodCountAnalysisResponse;
import de.hemolytix.server.model.CompleteBloodCount;
import de.hemolytix.server.model.Patient;
import de.hemolytix.server.model.ReferenceRange;
import de.hemolytix.server.repository.CompleteBloodCountRepository;
import de.hemolytix.server.repository.ReferenceRangeRepository;
import de.hemolytix.server.types.EvaluationStatus;
import de.hemolytix.server.types.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BloodAnalysisServiceTest {

    @Mock
    private ReferenceRangeRepository referenceRangeRepository;

    @Mock
    private CompleteBloodCountRepository cbcRepository;

    @InjectMocks
    private BloodAnalysisService bloodAnalysisService;

    private Patient malePatient;
    private CompleteBloodCount currentCbc;

    @BeforeEach
    void setUp() {
        malePatient = new Patient();
        malePatient.setFirstName("Max");
        malePatient.setLastName("Mustermann");
        malePatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        malePatient.setId(100L);
        malePatient.setGender(Gender.MALE);

        currentCbc = new CompleteBloodCount();
        currentCbc.setId(1L);
        currentCbc.setPatient(malePatient);
        currentCbc.setRecordedAt(LocalDateTime.of(2026, 5, 15, 10, 0));
        currentCbc.setHemoglobin(10.0);
        currentCbc.setErythrocytes(4.5);
    }

    @Test
    @DisplayName("Sollte Hämoglobin als LOW bewerten und das Patienten-Alter korrekt an Repository übergeben")
    void analyze_HemoglobinLow_ReturnsLowStatusAndCorrectAge() {
        // Arrange (1990-01-01 bis 2026-05-15 = 36 Jahre)
        ReferenceRange hbRange = new ReferenceRange("hemoglobin", Gender.MALE, 18, 99, 13.5, 17.5, "g/dl");
        ReferenceRange eryRange = new ReferenceRange("erythrocytes", Gender.MALE, 18, 99, 4.3, 5.9, "M/µl");

        when(referenceRangeRepository.findMatchingRange(eq("hemoglobin"), eq(Gender.MALE), eq(36)))
                .thenReturn(Optional.of(hbRange));
        when(referenceRangeRepository.findMatchingRange(eq("erythrocytes"), eq(Gender.MALE), eq(36)))
                .thenReturn(Optional.of(eryRange));

        // Act
        BloodCountAnalysisResponse result = bloodAnalysisService.analyze(currentCbc);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.patientId());

        assertNotNull(result.hemoglobin());
        assertEquals(10.0, result.hemoglobin().value());
        assertEquals("g/dL", result.hemoglobin().unit());
        assertEquals(EvaluationStatus.LOW, result.hemoglobin().status());

        assertNotNull(result.erythrocytes());
        assertEquals(4.5, result.erythrocytes().value());
        assertEquals(EvaluationStatus.NORMAL, result.erythrocytes().status());

        verify(referenceRangeRepository).findMatchingRange("hemoglobin", Gender.MALE, 36);
    }
}