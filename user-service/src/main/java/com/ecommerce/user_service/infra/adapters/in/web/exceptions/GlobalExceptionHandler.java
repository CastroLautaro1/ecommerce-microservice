package com.ecommerce.user_service.infra.adapters.in.web.exceptions;

import com.ecommerce.user_service.domain.exceptions.*;
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

    // Errores de Validación de Dominio -> 400 Bad Request (o 422 Unprocessable Entity)
    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDomainValidation(DomainValidationException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    // Credenciales Inválidas (Login fallido) -> 401 Unauthorized
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    // Usuario No Encontrado -> 404 Not Found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // Duplicados (Email/Username ya existe) -> 409 Conflict
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // Cuenta Desactivada -> 403 Forbidden
    @ExceptionHandler(AccountDeactivatedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountDeactivated(AccountDeactivatedException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    // 400 BAD REQUEST -> Errores de serializacion/deserializacion del JSON
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        // Spring busca automáticamente la causa más profunda (el DomainValidationException)
        Throwable mostSpecificCause = ex.getMostSpecificCause();

        if (mostSpecificCause.getClass().getPackageName().startsWith("com.ecommerce.user_service.domain")) {
            return buildErrorResponse(mostSpecificCause.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
        }

        return buildErrorResponse("El cuerpo de la petición (JSON) contiene un formato inválido", HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    // Fallback para cualquier excepción no controlada -> 500 Internal Server Error
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
