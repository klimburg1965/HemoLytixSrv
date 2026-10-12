package de.hemolytix.server.mapper;

import de.hemolytix.server.dto.CreateBloodCountRequest;
import de.hemolytix.server.model.CompleteBloodCount;
import de.hemolytix.server.model.Patient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CompleteBloodCountMapper {

    public CompleteBloodCount toEntity(CreateBloodCountRequest request, Patient patient) {
        if (request == null) {
            return null;
        }

        CompleteBloodCount entity = new CompleteBloodCount();
        entity.setPatient(patient);

        // Falls kein Zeitpunkt mitgegeben wurde, Fallback auf JETZT
        entity.setRecordedAt(request.recordedAt() != null ? request.recordedAt() : LocalDateTime.now());

        // Rotes Blutbild
        entity.setHemoglobin(request.hemoglobin());
        entity.setErythrocytes(request.erythrocytes());
        entity.setHematocrit(request.hematocrit());

        // Indizes
        entity.setMcv(request.mcv());
        entity.setMch(request.mch());
        entity.setMchc(request.mchc());

        // Leukozyten & Thrombozyten
        entity.setLeukocytes(request.leukocytes());
        entity.setPlatelets(request.platelets());

        // Differenzialblutbild
        entity.setNeutrophilsRel(request.neutrophilsRel());
        entity.setLymphocytesRel(request.lymphocytesRel());
        entity.setMonocytesRel(request.monocytesRel());
        entity.setEosinophilsRel(request.eosinophilsRel());
        entity.setBasophilsRel(request.basophilsRel());

        entity.setNotes(request.notes());

        return entity;
    }
}