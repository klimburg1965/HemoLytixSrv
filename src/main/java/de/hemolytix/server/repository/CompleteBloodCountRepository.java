package de.hemolytix.server.repository;

import de.hemolytix.server.model.CompleteBloodCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompleteBloodCountRepository extends JpaRepository<CompleteBloodCount, Long> {

    // Spring Data generiert die SQL-Abfrage über die Verschachtelung automatisiert:
    // SELECT * FROM lab_results WHERE patient_id = ?
    List<CompleteBloodCount> findByPatientId(Long patientId);

    Optional<CompleteBloodCount> findFirstByPatientIdAndRecordedAtBeforeOrderByRecordedAtDesc(
            Long patientId,
            LocalDateTime recordedAt
    );
}