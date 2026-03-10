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
     * @param dto datos del nuevo usuario (nombre, email, password, iaModeloSeleccionadoId)
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
}
