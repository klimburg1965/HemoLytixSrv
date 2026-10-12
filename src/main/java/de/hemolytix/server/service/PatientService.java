package de.hemolytix.server.service;

import de.hemolytix.server.dto.CreatePatientRequest;
import de.hemolytix.server.dto.PatientResponse;
import de.hemolytix.server.dto.UpdatePatientRequest;
import de.hemolytix.server.exception.ResourceNotFoundException;
import de.hemolytix.server.mapper.PatientMapper;
import de.hemolytix.server.model.Patient;
import de.hemolytix.server.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    // --- GET ALL ---
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(patientMapper::toResponse)
                .toList();
    }

    // --- GET BY ID ---
    public PatientResponse getPatientById(Long id) {
        Patient patient = findPatientOrThrow(id);
        return patientMapper.toResponse(patient);
    }

    // --- CREATE (POST) ---
    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {
        Patient patient = patientMapper.toEntity(request);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(savedPatient);
    }

    // --- UPDATE (PUT) ---
    @Transactional
    public PatientResponse updatePatient(Long id, UpdatePatientRequest request) {
        Patient patient = findPatientOrThrow(id);
        patientMapper.updateEntityFromRequest(request, patient);
        // Durch @Transactional reicht das Aktualisieren des Objekts; save() ist optional
        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(updatedPatient);
    }

    // --- DELETE ---
    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient mit ID " + id + " wurde nicht gefunden.");
        }
        patientRepository.deleteById(id);
    }

    // --- HELPER METHOD ---
    private Patient findPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient mit ID " + id + " wurde nicht gefunden."));
    }
}