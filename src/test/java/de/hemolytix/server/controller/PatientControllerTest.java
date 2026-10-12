package de.hemolytix.server.controller;

import de.hemolytix.server.dto.AddressResponse;
import de.hemolytix.server.dto.CreatePatientRequest;
import de.hemolytix.server.dto.PatientResponse;
import de.hemolytix.server.exception.PatientNotFoundException;
import de.hemolytix.server.service.CompleteBloodCountService;
import de.hemolytix.server.service.PatientService;
import de.hemolytix.server.service.PdfReportService;
import de.hemolytix.server.types.BloodType;
import de.hemolytix.server.types.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PatientControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @MockBean
    private CompleteBloodCountService completeBloodCountService;

    @MockBean
    private PdfReportService pdfReportService;

    private PatientResponse testPatientResponse;

    @BeforeEach
    void setUp() {
        AddressResponse addressResponse = new AddressResponse(
                1L,
                "Kaiser-Friedrich-Straße",
                "42",
                "41063",
                "Mönchengladbach"
        );

        // Aktualisiert auf den neuen Konstruktor mit den zusätzlichen Feldern
        testPatientResponse = new PatientResponse(
                1L,
                "Max",
                "Mustermann",
                LocalDate.of(1990, 5, 15),
                Gender.MALE,
                BloodType.A_POSITIVE,
                "Techniker Krankenkasse",
                "Dr. med. Thomas Klein",
                addressResponse,
                0
        );
    }

    // --- GET /api/v1/patients ---
    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAllPatients_ShouldReturnListOfPatients() throws Exception {
        when(patientService.getAllPatients()).thenReturn(List.of(testPatientResponse));

        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Max")))
                .andExpect(jsonPath("$[0].lastName", is("Mustermann")))
                .andExpect(jsonPath("$[0].healthInsuranceCompany", is("Techniker Krankenkasse")))
                .andExpect(jsonPath("$[0].address.city", is("Mönchengladbach")));

        verify(patientService, times(1)).getAllPatients();
    }

    // --- GET /api/v1/patients/{id} ---
    @Test
    @WithMockUser(roles = "DOCTOR")
    void getPatientById_WhenFound_ShouldReturnPatient() throws Exception {
        when(patientService.getPatientById(1L)).thenReturn(testPatientResponse);

        mockMvc.perform(get("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Max")))
                .andExpect(jsonPath("$.gender", is("MALE")))
                .andExpect(jsonPath("$.familyDoctor", is("Dr. med. Thomas Klein")));

        verify(patientService, times(1)).getPatientById(1L);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getPatientById_WhenNotFound_ShouldReturn404() throws Exception {
        when(patientService.getPatientById(99L)).thenThrow(new PatientNotFoundException(99L));

        mockMvc.perform(get("/api/v1/patients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Ressource nicht gefunden"));

        verify(patientService, times(1)).getPatientById(99L);
    }

    // --- POST /api/v1/patients ---
    @Test
    @WithMockUser(roles = "DOCTOR")
    void createPatient_ShouldSaveAndReturnCreatedPatient() throws Exception {
        when(patientService.createPatient(any(CreatePatientRequest.class))).thenReturn(testPatientResponse);

        String jsonPayload = """
                {
                    "firstName": "Max",
                    "lastName": "Mustermann",
                    "birthDate": "1990-05-15",
                    "gender": "MALE",
                    "bloodType": "A+",
                    "healthInsuranceCompany": "Techniker Krankenkasse",
                    "familyDoctor": "Dr. med. Thomas Klein",
                    "address": {
                        "street": "Kaiser-Friedrich-Straße",
                        "houseNumber": "42",
                        "zipCode": "41063",
                        "city": "Mönchengladbach"
                    }
                }
                """;

        mockMvc.perform(post("/api/v1/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Max")))
                .andExpect(jsonPath("$.healthInsuranceCompany", is("Techniker Krankenkasse")));

        verify(patientService, times(1)).createPatient(any(CreatePatientRequest.class));
    }

    // --- DELETE /api/v1/patients/{id} ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePatient_WhenExists_ShouldReturn204() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/api/v1/patients/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }
}