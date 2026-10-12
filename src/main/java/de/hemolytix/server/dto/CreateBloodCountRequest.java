package de.hemolytix.server.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateBloodCountRequest(

        @NotNull(message = "Erfassungsdatum ist erforderlich.")
        @PastOrPresent(message = "Erfassungsdatum darf nicht in der Zukunft liegen.")
        LocalDateTime recordedAt,

        // --- Rotes Blutbild ---
        @Positive(message = "Hämoglobin muss positiv sein.")
        Double hemoglobin,

        @Positive(message = "Erythrozyten müssen positiv sein.")
        Double erythrocytes,

        @Positive(message = "Hämatokrit muss positiv sein.")
        Double hematocrit,

        // --- Erythrozyten-Indizes ---
        @Positive(message = "MCV muss positiv sein.")
        Double mcv,

        @Positive(message = "MCH muss positiv sein.")
        Double mch,

        @Positive(message = "MCHC muss positiv sein.")
        Double mchc,

        // --- Leukozyten & Thrombozyten ---
        @Positive(message = "Leukozyten müssen positiv sein.")
        Double leukocytes,

        @Positive(message = "Thrombozyten müssen positiv sein.")
        Double platelets,

        // --- Differenzialblutbild (%) ---
        @PositiveOrZero(message = "Neutrophile müssen positiv oder 0 sein.")
        Double neutrophilsRel,

        @PositiveOrZero(message = "Lymphozyten müssen positiv oder 0 sein.")
        Double lymphocytesRel,

        @PositiveOrZero(message = "Monozyten müssen positiv oder 0 sein.")
        Double monocytesRel,

        @PositiveOrZero(message = "Eosinophile müssen positiv oder 0 sein.")
        Double eosinophilsRel,

        @PositiveOrZero(message = "Basophile müssen positiv oder 0 sein.")
        Double basophilsRel,

        @Size(max = 1000, message = "Notizen dürfen maximal 1000 Zeichen lang sein.")
        String notes
) {}