package de.hemolytix.server.controller;

import de.hemolytix.server.dto.BloodCountAnalysisResponse;
import de.hemolytix.server.dto.CreateBloodCountRequest;
import de.hemolytix.server.service.CompleteBloodCountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/complete-blood-counts")
@Tag(name = "Complete Blood Count Management", description = "Endpoints for recorded patient blood values and lab analysis")
public class CompleteBloodCountController {

    private final CompleteBloodCountService countService;

    public CompleteBloodCountController(CompleteBloodCountService countService) {
        this.countService = countService;
    }

    // --- GET /api/v1/patients/{patientId}/complete-blood-counts ---
    @GetMapping
    @Operation(summary = "Get all complete blood counts for a specific patient",
            description = "Retrieves all analyzed blood counts associated with the specified patient ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved blood count analyses"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<BloodCountAnalysisResponse>> getCompleteBloodCountsByPatient(@PathVariable Long patientId) {
        List<BloodCountAnalysisResponse> responses = countService.getAnalysesByPatientId(patientId);
        return ResponseEntity.ok(responses);
    }

    // --- GET /api/v1/patients/{patientId}/complete-blood-counts/{id}/analysis ---
    @GetMapping("/{id}/analysis")
    @Operation(summary = "Get analysis for a specific blood count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved analysis"),
            @ApiResponse(responseCode = "404", description = "Blood count or patient not found")
    })
    public ResponseEntity<BloodCountAnalysisResponse> getAnalysis(@PathVariable Long patientId, @PathVariable Long id) {
        BloodCountAnalysisResponse analysis = countService.getAnalysisById(id);
        return ResponseEntity.ok(analysis);
    }

    // --- GET /api/v1/patients/{patientId}/complete-blood-counts/analysis ---
    @GetMapping("/analysis")
    @Operation(summary = "Get all blood count analyses for a specific patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved patient analyses"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<BloodCountAnalysisResponse>> getPatientAnalyses(@PathVariable Long patientId) {
        List<BloodCountAnalysisResponse> responses = countService.getAnalysesByPatientId(patientId);
        return ResponseEntity.ok(responses);
    }

    // --- POST /api/v1/patients/{patientId}/complete-blood-counts ---
    @PostMapping
    @Operation(summary = "Add a new complete blood count for a patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blood count successfully created and analyzed"),
            @ApiResponse(responseCode = "400", description = "Invalid input payload"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<BloodCountAnalysisResponse> createCompleteBloodCount(
            @PathVariable Long patientId,
            @Valid @RequestBody CreateBloodCountRequest request) {

        BloodCountAnalysisResponse created = countService.createCount(patientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}