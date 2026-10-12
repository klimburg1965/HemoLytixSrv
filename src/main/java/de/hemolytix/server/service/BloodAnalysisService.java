package de.hemolytix.server.service;

import de.hemolytix.server.dto.BloodCountAnalysisResponse;
import de.hemolytix.server.dto.EvaluatedValue;
import de.hemolytix.server.model.CompleteBloodCount;
import de.hemolytix.server.model.Patient;
import de.hemolytix.server.model.ReferenceRange;
import de.hemolytix.server.repository.CompleteBloodCountRepository;
import de.hemolytix.server.repository.ReferenceRangeRepository;
import de.hemolytix.server.types.BloodParameter;
import de.hemolytix.server.types.EvaluationStatus;
import de.hemolytix.server.types.Gender;
import de.hemolytix.server.types.ParameterTrend;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
public class BloodAnalysisService {

    private final ReferenceRangeRepository referenceRangeRepository;
    private final CompleteBloodCountRepository cbcRepository;

    // Relative Änderungsschwelle für Trend-Erkennung (z.B. 3% Abweichung)
    private static final double TREND_THRESHOLD = 0.03;

    public BloodAnalysisService(ReferenceRangeRepository referenceRangeRepository,
                                CompleteBloodCountRepository cbcRepository) {
        this.referenceRangeRepository = referenceRangeRepository;
        this.cbcRepository = cbcRepository;
    }

    @Transactional(readOnly = true)
    public BloodCountAnalysisResponse analyze(CompleteBloodCount currentCbc) {
        Patient patient = currentCbc.getPatient();
        Gender gender = (patient != null && patient.getGender() != null) ? patient.getGender() : Gender.UNSPECIFIED;
        int age = calculateAgeAtRecording(patient, currentCbc);

        // Vorheriges Blutbild für Trend-Berechnung laden
        Optional<CompleteBloodCount> previousCbcOpt = cbcRepository
                .findFirstByPatientIdAndRecordedAtBeforeOrderByRecordedAtDesc(
                        patient != null ? patient.getId() : null,
                        currentCbc.getRecordedAt()
                );

        // --- Rotes Blutbild ---
        EvaluatedValue hb = createEvaluatedValue("hemoglobin", currentCbc.getHemoglobin(), "g/dL", gender, age,
                previousCbcOpt.map(CompleteBloodCount::getHemoglobin).orElse(null));
        EvaluatedValue ery = createEvaluatedValue("erythrocytes", currentCbc.getErythrocytes(), "M/µL", gender, age,
                previousCbcOpt.map(CompleteBloodCount::getErythrocytes).orElse(null));
        EvaluatedValue hk = createEvaluatedValue("hematocrit", currentCbc.getHematocrit(), "%", gender, age,
                previousCbcOpt.map(CompleteBloodCount::getHematocrit).orElse(null));

        // --- Erythrozyten-Indizes ---
        EvaluatedValue mcv = createEvaluatedValue("mcv", currentCbc.getMcv(), "fL", gender, age, null);
        EvaluatedValue mch = createEvaluatedValue("mch", currentCbc.getMch(), "pg", gender, age, null);
        EvaluatedValue mchc = createEvaluatedValue("mchc", currentCbc.getMchc(), "g/dL", gender, age, null);

        // --- Leukozyten & Thrombozyten ---
        EvaluatedValue leuko = createEvaluatedValue("leukocytes", currentCbc.getLeukocytes(), "K/µL", gender, age,
                previousCbcOpt.map(CompleteBloodCount::getLeukocytes).orElse(null));
        EvaluatedValue thrombo = createEvaluatedValue(BloodParameter.PLATELETS.getDbKey(), currentCbc.getPlatelets(), "K/µL", gender, age,
                previousCbcOpt.map(CompleteBloodCount::getPlatelets).orElse(null));

        // --- Differenzialblutbild (%) ---
        EvaluatedValue neutrophilsRel = createEvaluatedValue("neutrophilsRel", currentCbc.getNeutrophilsRel(), "%", gender, age, null);
        EvaluatedValue lymphocytesRel = createEvaluatedValue("lymphocytesRel", currentCbc.getLymphocytesRel(), "%", gender, age, null);
        EvaluatedValue monocytesRel = createEvaluatedValue("monocytesRel", currentCbc.getMonocytesRel(), "%", gender, age, null);
        EvaluatedValue eosinophilsRel = createEvaluatedValue("eosinophilsRel", currentCbc.getEosinophilsRel(), "%", gender, age, null);
        EvaluatedValue basophilsRel = createEvaluatedValue("basophilsRel", currentCbc.getBasophilsRel(), "%", gender, age, null);

        // Zusammenbau der Response
        return new BloodCountAnalysisResponse(
                currentCbc.getId(),
                patient != null ? patient.getId() : null,
                currentCbc.getRecordedAt(),

                // Rotes Blutbild
                hb,
                ery,
                hk,

                // Indizes
                mcv,
                mch,
                mchc,

                // Leukozyten & Thrombozyten
                leuko,
                thrombo,

                // Differenzialblutbild
                neutrophilsRel,
                lymphocytesRel,
                monocytesRel,
                eosinophilsRel,
                basophilsRel,

                currentCbc.getNotes()
        );
    }

    // Hilfsmethode zum Erzeugen von EvaluatedValue-Objekten
    private EvaluatedValue createEvaluatedValue(String parameterName, Double value, String unit, Gender gender, int age, Double previousValue) {
        if (value == null) {
            return null;
        }

        EvaluationStatus status = evaluateParameter(parameterName, value, gender, age);

        // Hinweis: Falls EvaluatedValue auch Trends aufnehmen soll, kann das hier ergänzt werden.
        return EvaluatedValue.of(value, unit, status);
    }

    private EvaluationStatus evaluateParameter(String parameterName, Double value, Gender gender, int age) {
        if (value == null) {
            return EvaluationStatus.UNKNOWN;
        }

        Optional<ReferenceRange> rangeOpt = referenceRangeRepository.findMatchingRange(parameterName, gender, age);
        if (rangeOpt.isEmpty()) {
            return EvaluationStatus.UNKNOWN;
        }

        ReferenceRange range = rangeOpt.get();
        if (range.getMinValue() != null && value < range.getMinValue()) {
            return EvaluationStatus.LOW;
        }
        if (range.getMaxValue() != null && value > range.getMaxValue()) {
            return EvaluationStatus.HIGH;
        }
        return EvaluationStatus.NORMAL;
    }

    private ParameterTrend calculateTrend(Double currentValue, Double previousValue) {
        if (currentValue == null || previousValue == null) {
            return ParameterTrend.NOT_DETERMINABLE;
        }

        double diff = currentValue - previousValue;
        double relativeChange = diff / previousValue;

        if (relativeChange > TREND_THRESHOLD) {
            return ParameterTrend.RISING;
        } else if (relativeChange < -TREND_THRESHOLD) {
            return ParameterTrend.FALLING;
        } else {
            return ParameterTrend.STABLE;
        }
    }

    private int calculateAgeAtRecording(Patient patient, CompleteBloodCount cbc) {
        if (patient == null || patient.getDateOfBirth() == null) {
            return 30; // Default-Fallback
        }
        LocalDate recordedDate = cbc.getRecordedAt() != null
                ? cbc.getRecordedAt().toLocalDate()
                : LocalDate.now();

        return Period.between(patient.getDateOfBirth(), recordedDate).getYears();
    }
}