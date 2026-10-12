package de.hemolytix.server.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BloodType {
    A_POSITIVE("A+"), A_NEGATIVE("A-"),
    B_POSITIVE("B+"), B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"), AB_NEGATIVE("AB-"),
    O_POSITIVE("0+"), O_NEGATIVE("0-");

    private final String displayValue;

    BloodType(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue // Sorgt dafür, dass Jackson "A+" als JSON ausgibt
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator // Erlaubt Jackson, "A+" wieder in BloodType.A_POSITIVE zu parsen
    public static BloodType fromString(String text) {
        for (BloodType b : BloodType.values()) {
            if (b.displayValue.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unbekannter Bluttyp: " + text);
    }
}