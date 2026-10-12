package de.hemolytix.server.model;

import de.hemolytix.server.types.Gender;
import jakarta.persistence.*;

@Entity
@Table(name = "reference_ranges")
public class ReferenceRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Entspricht den Feldnamen in CompleteBloodCount (z. B. "hemoglobin", "leukocytes")
    @Column(name = "parameter_name", nullable = false, length = 50)
    private String parameterName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 20)
    private Gender gender; // MALE, FEMALE, UNSPECIFIED

    @Column(name = "min_age_years")
    private Integer minAgeYears; // Nullable (z. B. 18)

    @Column(name = "max_age_years")
    private Integer maxAgeYears; // Nullable (z. B. 99)

    @Column(name = "min_value")
    private Double minValue; // Unterer Grenzwert

    @Column(name = "max_value")
    private Double maxValue; // Oberer Grenzwert

    @Column(length = 20)
    private String unit; // z. B. "g/dl", "G/l", "%"

    public ReferenceRange() {}

    public ReferenceRange(String parameterName, Gender gender, Integer minAgeYears, Integer maxAgeYears, Double minValue, Double maxValue, String unit) {
        this.parameterName = parameterName;
        this.gender = gender;
        this.minAgeYears = minAgeYears;
        this.maxAgeYears = maxAgeYears;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.unit = unit;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getMinAgeYears() {
        return minAgeYears;
    }

    public void setMinAgeYears(Integer minAgeYears) {
        this.minAgeYears = minAgeYears;
    }

    public Integer getMaxAgeYears() {
        return maxAgeYears;
    }

    public void setMaxAgeYears(Integer maxAgeYears) {
        this.maxAgeYears = maxAgeYears;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Alias-Methoden für Abwärtskompatibilität
    public Double getMinVal() {
        return minValue;
    }

    public Double getMaxVal() {
        return maxValue;
    }
}