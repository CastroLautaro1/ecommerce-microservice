package com.ecommerce.user_service.infra.adapters.in.web.exceptions;

import com.ecommerce.user_service.domain.exceptions.AccountDeactivatedException;
import com.ecommerce.user_service.domain.exceptions.DomainValidationException;
import com.ecommerce.user_service.domain.exceptions.UserAlreadyExistsException;
import com.ecommerce.user_service.domain.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Errores de Validación de Dominio -> 400 Bad Request (o 422 Unprocessable Entity)
    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDomainValidation(DomainValidationException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    // 2. Usuario No Encontrado -> 404 Not Found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // 3. Duplicados (Email/Username ya existe) -> 409 Conflict
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // 4. Cuenta Desactivada -> 403 Forbidden
    @ExceptionHandler(AccountDeactivatedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountDeactivated(AccountDeactivatedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    // 5. Fallback para cualquier excepción no controlada -> 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse("Ocurrió un error interno en el servidor", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    // Método auxiliar
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status, String path) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", path);

        return ResponseEntity.status(status).body(errorResponse);
    }
}
