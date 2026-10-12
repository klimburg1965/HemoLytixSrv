package de.hemolytix.server.controller;

import de.hemolytix.server.dto.BloodCountAnalysisResponse;
import de.hemolytix.server.dto.CreatePatientRequest;
import de.hemolytix.server.dto.PatientResponse;
import de.hemolytix.server.dto.UpdatePatientRequest;
import de.hemolytix.server.service.CompleteBloodCountService;
import de.hemolytix.server.service.PatientService;
import de.hemolytix.server.service.PdfReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patient Management", description = "Endpoints for managing patient master data and reports")
public class PatientController {

    private final PatientService patientService;
    private final CompleteBloodCountService countService;
    private final PdfReportService pdfReportService;

    public PatientController(PatientService patientService,
                             CompleteBloodCountService countService,
                             PdfReportService pdfReportService) {
        this.patientService = patientService;
        this.countService = countService;
        this.pdfReportService = pdfReportService;
    }

    // --- GET /api/v1/patients ---
    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieves a list of all registered patients.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    // --- GET /api/v1/patients/{id} ---
    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieves detailed information for a single patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        PatientResponse patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    // --- POST /api/v1/patients ---
    @PostMapping
    @Operation(summary = "Create a new patient", description = "Registers a new patient in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        PatientResponse createdPatient = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    // --- PUT /api/v1/patients/{id} ---
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing patient", description = "Updates details of an existing patient by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request) {
        PatientResponse updatedPatient = patientService.updatePatient(id, request);
        return ResponseEntity.ok(updatedPatient);
    }

    // --- DELETE /api/v1/patients/{id} ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Removes a patient from the system by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    // --- GET /api/v1/patients/{id}/report ---
    @GetMapping(value = "/{id}/report", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Generate PDF lab report", description = "Generates and downloads a comprehensive PDF medical report containing all blood count analyses for the specified patient.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF successfully generated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PDF_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found"
            )
    })
    public ResponseEntity<InputStreamResource> getPatientPdfReport(@PathVariable Long id) {
        // 1. Patient laden
        PatientResponse patient = patientService.getPatientById(id);

        // 2. Analysen des Patienten abrufen
        List<BloodCountAnalysisResponse> analyses = countService.getAnalysesByPatientId(id);

        // 3. PDF generieren lassen
        ByteArrayInputStream pdfStream = pdfReportService.generateLabReport(patient, analyses);

        // 4. HTTP-Header für den Dateidownload setzen
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename("befund_patient_" + id + ".pdf")
                        .build()
        );

        headers.setContentLength(pdfStream.available());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}