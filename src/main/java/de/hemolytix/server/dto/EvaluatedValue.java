package de.hemolytix.server.dto;

import de.hemolytix.server.types.EvaluationStatus; // NORMAL, HIGH, LOW

public record EvaluatedValue(
        Double value,
        String unit,
        EvaluationStatus status
) {
    public static EvaluatedValue of(Double value, String unit, EvaluationStatus status) {
        if (value == null) {
            return null;
        }
        return new EvaluatedValue(value, unit, status != null ? status : EvaluationStatus.NORMAL);
    }
}