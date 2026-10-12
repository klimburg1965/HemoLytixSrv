package de.hemolytix.server.repository;

import de.hemolytix.server.model.ReferenceRange;
import de.hemolytix.server.types.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferenceRangeRepository extends JpaRepository<ReferenceRange, Long> {

    @Query("""
        SELECT r FROM ReferenceRange r 
        WHERE r.parameterName = :parameterName 
          AND (r.gender = :gender OR r.gender = de.hemolytix.server.types.Gender.UNSPECIFIED) 
          AND (r.minAgeYears IS NULL OR r.minAgeYears <= :age) 
          AND (r.maxAgeYears IS NULL OR r.maxAgeYears >= :age)
    """)
    Optional<ReferenceRange> findMatchingRange(
            @Param("parameterName") String parameterName,
            @Param("gender") Gender gender,
            @Param("age") int age
    );
}