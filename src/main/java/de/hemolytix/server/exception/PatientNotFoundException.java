package de.hemolytix.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("Patient mit ID " + id + " wurde nicht gefunden.");
    }

    public PatientNotFoundException(String message) {
        super(message);
    }
}