package de.hemolytix.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handling für Entität nicht gefunden (PatientNotFoundException & CompleteBloodCountNotFoundException)
    @ExceptionHandler({PatientNotFoundException.class, CompleteBloodCountNotFoundException.class, ResourceNotFoundException.class})
    public ProblemDetail handleNotFoundException(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Ressource nicht gefunden");
        problemDetail.setType(URI.create("https://hemolytix.de/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 2. Handling für Bean Validation Fehler (@Valid im RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Die übergebenen Daten sind ungültig."
        );
        problemDetail.setTitle("Validierungsfehler");
        problemDetail.setType(URI.create("https://hemolytix.de/errors/bad-request"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("invalidFields", errors); // Detaillierte Feldfehler mitsenden!

        return problemDetail;
    }

    // 3. Catch-All für alle unerwarteten Systemfehler (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ein unerwarteter Serverfehler ist aufgetreten."
        );
        problemDetail.setTitle("Interner Serverfehler");
        problemDetail.setType(URI.create("https://hemolytix.de/errors/internal-server-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        // Hinweis: ex.getMessage() im Prod-Betrieb nicht ungefiltert ausgeben (Security risk)
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Das übergebene JSON ist fehlerhaft oder enthält ungültige Werte/Enums."
        );
        problemDetail.setTitle("Ungültiges JSON-Format");
        problemDetail.setType(URI.create("https://hemolytix.de/errors/bad-request"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}