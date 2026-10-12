package de.hemolytix.server.controller;

import de.hemolytix.server.dto.BloodCountAnalysisResponse;
import de.hemolytix.server.dto.EvaluatedValue;
import de.hemolytix.server.service.CompleteBloodCountService;
import de.hemolytix.server.types.EvaluationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CompleteBloodCountControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompleteBloodCountService countService;

    @Test
    @WithMockUser
    void analyzeCbc_WhenExists_ShouldReturnAnalysis() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        BloodCountAnalysisResponse analysisResponse = new BloodCountAnalysisResponse(
                1L,
                100L,
                now,

                // --- Rotes Blutbild ---
                EvaluatedValue.of(14.5, "g/dL", EvaluationStatus.NORMAL),
                null,
                null,

                // --- Indizes ---
                null,
                null,
                null,

                // --- Leukozyten & Thrombozyten ---
                null,
                null,

                // --- Differenzialblutbild ---
                null,
                null,
                null,
                null,
                null,

                "Keine Besonderheiten"
        );

        when(countService.getAnalysisById(1L)).thenReturn(analysisResponse);

        mockMvc.perform(get("/api/v1/patients/100/complete-blood-counts/1/analysis")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(100))
                .andExpect(jsonPath("$.hemoglobin.value").value(14.5))
                .andExpect(jsonPath("$.hemoglobin.unit").value("g/dL"))
                .andExpect(jsonPath("$.hemoglobin.status").value("NORMAL"))
                .andExpect(jsonPath("$.notes").value("Keine Besonderheiten"));
    }
}