package de.hemolytix.server.service;

import de.hemolytix.server.dto.BloodCountAnalysisResponse;
import de.hemolytix.server.dto.CreateBloodCountRequest;
import de.hemolytix.server.exception.CompleteBloodCountNotFoundException;
import de.hemolytix.server.exception.PatientNotFoundException;
import de.hemolytix.server.mapper.CompleteBloodCountMapper;
import de.hemolytix.server.model.CompleteBloodCount;
import de.hemolytix.server.model.Patient;
import de.hemolytix.server.repository.CompleteBloodCountRepository;
import de.hemolytix.server.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CompleteBloodCountService {

    private final CompleteBloodCountRepository completeBloodCountRepository;
    private final PatientRepository patientRepository;
    private final CompleteBloodCountMapper countMapper;
    private final BloodAnalysisService analysisService;

    public CompleteBloodCountService(CompleteBloodCountRepository completeBloodCountRepository,
                                     PatientRepository patientRepository,
                                     CompleteBloodCountMapper countMapper,
                                     BloodAnalysisService analysisService) {
        this.completeBloodCountRepository = completeBloodCountRepository;
        this.patientRepository = patientRepository;
        this.countMapper = countMapper;
        this.analysisService = analysisService;
    }

    // --- READ OPERATIONS ---

    public BloodCountAnalysisResponse getAnalysisById(Long id) {
        return completeBloodCountRepository.findById(id)
                .map(analysisService::analyze)
                .orElseThrow(() -> new CompleteBloodCountNotFoundException(id));
    }

    public List<BloodCountAnalysisResponse> getAnalysesByPatientId(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new PatientNotFoundException(patientId);
        }
        return completeBloodCountRepository.findByPatientId(patientId).stream()
                .map(analysisService::analyze)
                .toList();
    }

    // --- WRITE OPERATIONS ---

    @Transactional
    public BloodCountAnalysisResponse createCount(Long patientId, CreateBloodCountRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        // 1. DTO -> Entity mappen
        CompleteBloodCount entity = countMapper.toEntity(request, patient);

        // 2. In Datenbank speichern
        CompleteBloodCount savedEntity = completeBloodCountRepository.save(entity);

        // 3. Auswerten & als Response zurückgeben
        return analysisService.analyze(savedEntity);
    }
}