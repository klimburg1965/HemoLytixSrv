package de.hemolytix.server.dto;

import java.time.LocalDateTime;

public record BloodCountAnalysisResponse(
        Long id,
        Long patientId,
        LocalDateTime recordedAt,

        // --- Rotes Blutbild ---
        EvaluatedValue hemoglobin,
        EvaluatedValue erythrocytes,
        EvaluatedValue hematocrit,

        // --- Indizes ---
        EvaluatedValue mcv,
        EvaluatedValue mch,
        EvaluatedValue mchc,

        // --- Leukozyten & Thrombozyten ---
        EvaluatedValue leukocytes,
        EvaluatedValue platelets,

        // --- Differenzialblutbild ---
        EvaluatedValue neutrophilsRel,
        EvaluatedValue lymphocytesRel,
        EvaluatedValue monocytesRel,
        EvaluatedValue eosinophilsRel,
        EvaluatedValue basophilsRel,

        String notes
) {}