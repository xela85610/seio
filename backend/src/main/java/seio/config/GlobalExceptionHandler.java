package seio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400 - règles métier / validations manuelles
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(baseBody("VALIDATION_ERROR", ex.getMessage()));
    }

    // 404 - ressource absente
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(baseBody("NOT_FOUND", ex.getMessage()));
    }

    // 400 - JSON malformé ou type invalide
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNotReadable(HttpMessageNotReadableException ex) {
        log.debug("Corps de requête illisible: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(baseBody(
                "BAD_REQUEST",
                "Corps de requête invalide ou illisible (JSON)."
        ));
    }

    // 400 - Bean Validation sur @Valid DTO (erreurs de binding)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgNotValid(MethodArgumentNotValidException ex) {
        List<Map<String, String>> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::fieldErrorToMap)
                .collect(Collectors.toList());

        Map<String, Object> body = baseBody("VALIDATION_ERROR", "Des champs sont invalides");
        body.put("details", details);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 - Bean Validation sur paramètres (@Validated au niveau contrôleur/service)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> details = ex.getConstraintViolations().stream()
                .map(this::violationToMap)
                .collect(Collectors.toList());

        Map<String, Object> body = baseBody("VALIDATION_ERROR", "Des contraintes de validation sont violées");
        body.put("details", details);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 - Paramètre obligatoire manquant
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, Object> body = baseBody("BAD_REQUEST",
                "Paramètre requis manquant: " + ex.getParameterName());
        return ResponseEntity.badRequest().body(body);
    }

    // 500 - fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        // Log en erreur avec stacktrace; le message retourné reste générique (sécurité)
        log.error("Erreur serveur non gérée", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(baseBody("INTERNAL_ERROR", "Une erreur interne est survenue. Veuillez réessayer plus tard."));
    }

    // -------------------- Helpers --------------------

    private Map<String, Object> baseBody(String code, String message) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("error", code);
        map.put("message", message);
        map.put("timestamp", OffsetDateTime.now().toString());
        return map;
    }

    private Map<String, String> fieldErrorToMap(FieldError fe) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("field", fe.getField());
        m.put("message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide");
        if (fe.getRejectedValue() != null) {
            m.put("rejectedValue", String.valueOf(fe.getRejectedValue()));
        }
        return m;
    }

    private Map<String, String> violationToMap(ConstraintViolation<?> v) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("property", v.getPropertyPath() != null ? v.getPropertyPath().toString() : "");
        m.put("message", v.getMessage());
        if (v.getInvalidValue() != null) {
            m.put("invalidValue", String.valueOf(v.getInvalidValue()));
        }
        return m;
    }
}
