package com.daw.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.LoginRequestDTO;
import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.AuthResponseDTO;
import com.daw.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para las operaciones de autenticación.
 *
 * Todos los endpoints bajo la ruta /auth/** son públicos (configurado en SecurityConfig).
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registra un nuevo usuario y devuelve un token JWT para la autenticación.
     *
     * @param dto datos del nuevo usuario (nombre, email, password)
     * 
     * @return 201 Created con el token JWT y datos básicos del usuario
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UsuarioRequestDTO dto) {
        AuthResponseDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Inicia sesión con email y contraseña, devolviendo un token JWT.
     *
     * @param dto credenciales del usuario (email, password)
     * @return 200 Ok con el token JWT y datos básicos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok().body(authService.login(dto));
    }

    /**
     * Paso 1: Solicita la recuperación de contraseña enviando un código por email.
     */
    @PostMapping("/recuperar-password")
    public ResponseEntity<String> recuperarPassword(@Valid @RequestBody com.daw.dtos.request.RecuperarRequestDTO dto) {
        try {
            authService.solicitarRecuperacion(dto.getEmail());
            return ResponseEntity.ok().body("{\"mensaje\": \"Se ha enviado un código de recuperación a tu email.\"}");
        } catch (com.daw.exceptions.RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"mensaje\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Paso 2: Verifica que el código es válido para ese email.
     */
    @PostMapping("/verificar-codigo")
    public ResponseEntity<String> verificarCodigo(@Valid @RequestBody com.daw.dtos.request.VerificarCodigoRequestDTO dto) {
        boolean valido = authService.verificarCodigo(dto.getEmail(), dto.getCodigo());
        if (valido) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Código válido.\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"mensaje\": \"Código inválido o expirado.\"}");
        }
    }

    /**
     * Paso 3: Resetea la contraseña usando el código verificado.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody com.daw.dtos.request.ResetPasswordRequestDTO dto) {
        try {
            authService.resetearPassword(dto.getEmail(), dto.getCodigo(), dto.getNuevaPassword());
            return ResponseEntity.ok().body("{\"mensaje\": \"Contraseña actualizada correctamente.\"}");
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"mensaje\": \"Código inválido o expirado.\"}");
        }
    }
}
