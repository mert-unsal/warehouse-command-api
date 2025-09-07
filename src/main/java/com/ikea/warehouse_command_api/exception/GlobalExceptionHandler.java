package com.ikea.warehouse_command_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Resource not found");
        pd.setType(URI.create("https://httpstatuses.com/404"));
        addCommonProperties(pd, request, Map.of("error", "RESOURCE_NOT_FOUND"));
        return pd;
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ProblemDetail handleOptimistic(OptimisticLockingFailureException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Conflict: Optimistic Locking");
        pd.setType(URI.create("https://httpstatuses.com/409"));
        addCommonProperties(pd, request, Map.of("error", "OPTIMISTIC_LOCK_CONFLICT"));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("One or more request fields are invalid");
        pd.setType(URI.create("https://httpstatuses.com/400"));
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldErrorMap)
                .toList();
        pd.setProperty("errors", errors);
        addCommonProperties(pd, request, Map.of("error", "VALIDATION_ERROR"));
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleOther(Exception ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        pd.setTitle("Internal Server Error");
        pd.setType(URI.create("https://httpstatuses.com/500"));
        addCommonProperties(pd, request, Map.of("error", "INTERNAL_ERROR"));
        return pd;
    }

    private Map<String, String> toFieldErrorMap(FieldError fe) {
        return Map.of(
                "field", fe.getField(),
                "code", fe.getCode() != null ? fe.getCode() : "Invalid",
                "message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value"
        );
    }

    private void addCommonProperties(ProblemDetail pd, HttpServletRequest request, Map<String, Object> extra) {
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId != null && !correlationId.isBlank()) {
            pd.setProperty("correlationId", correlationId);
        }
        extra.forEach(pd::setProperty);
        pd.setProperty("path", request.getRequestURI());
    }
}
