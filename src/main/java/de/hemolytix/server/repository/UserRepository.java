package de.hemolytix.server.repository;

import de.hemolytix.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Sucht einen Benutzer anhand seines Eindeutigen Benutzernamens.
     * Spring Data JPA generiert die Abfrage automatisch anhand des Methodennamens.
     */
    Optional<User> findByUsername(String username);

    /**
     * Prüft, ob ein Benutzer mit dem angegebenen Benutzernamen bereits existiert.
     */
    boolean existsByUsername(String username);
}