package de.hemolytix.server.repository;

import de.hemolytix.server.model.Patient;
import de.hemolytix.server.types.BloodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Derived query method
    List<Patient> findByLastNameIgnoreCase(String lastName);

    List<Patient> findByBloodType(BloodType bloodType);
}