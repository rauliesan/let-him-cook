package com.daw.controllers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.daw.errors.MiError;
import com.daw.exceptions.OperacionInvalidaException;
import com.daw.exceptions.RecursoDuplicadoException;
import com.daw.exceptions.RecursoNoEncontradoException;

/**
 * Manejador global de excepciones.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestControllerAdvice
public class GlobalControllerAdvise {

    /**
     * Recurso no encontrado → 404
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<MiError> handleRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        MiError error = new MiError(HttpStatus.NOT_FOUND, LocalDateTime.now(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Recurso duplicado (ej. email ya registrado) → 409
     */
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<MiError> handleRecursoDuplicado(RecursoDuplicadoException ex) {
        MiError error = new MiError(HttpStatus.CONFLICT, LocalDateTime.now(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Errores de validación (@Valid) → 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MiError> handleValidation(MethodArgumentNotValidException ex) {
    	String mensaje = ex.getBindingResult().getFieldError().getDefaultMessage();
        MiError error = new MiError(HttpStatus.BAD_REQUEST, LocalDateTime.now(), mensaje, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Errores de Autorización (@PreAuthorize) → 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MiError> handleAccessDenied(AccessDeniedException ex, jakarta.servlet.http.HttpServletRequest request) {
        MiError error = new MiError(HttpStatus.FORBIDDEN, LocalDateTime.now(),
                "Acceso denegado. No tienes suficientes permisos para realizar esta acción.", request.getServletPath());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Credenciales incorrectas en el login → 401
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MiError> handleBadCredentials(BadCredentialsException ex, jakarta.servlet.http.HttpServletRequest request) {
        MiError error = new MiError(HttpStatus.UNAUTHORIZED, LocalDateTime.now(),
                "Credenciales incorrectas.", request.getServletPath());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Operación no permitida por lógica de negocio → 400
     */
    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<MiError> handleOperacionInvalida(OperacionInvalidaException ex, jakarta.servlet.http.HttpServletRequest request) {
        MiError error = new MiError(HttpStatus.BAD_REQUEST, LocalDateTime.now(),
                ex.getMessage(), request.getServletPath());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Cualquier otra excepción no controlada → 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MiError> handleGeneral(Exception ex, jakarta.servlet.http.HttpServletRequest request) {
        MiError error = new MiError(HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(),
                "Error interno del servidor.", request.getServletPath());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
