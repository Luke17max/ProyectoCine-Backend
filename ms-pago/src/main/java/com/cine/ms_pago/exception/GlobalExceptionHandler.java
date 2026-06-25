package com.cine.ms_pago.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidations(MethodArgumentNotValidException ex) {
        log.warn("Excepción de validación (400 Bad Request)");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        log.error("Excepción de negocio: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());

        if (ex.getMessage().contains("no existe") || ex.getMessage().contains("Ya existe un pago")) {
            // Manejo de dependencias de Feign o reglas de negocio (ej. Conflictos)
            HttpStatus status = ex.getMessage().contains("Ya existe") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(error);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}