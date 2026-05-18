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

/** Endpoints de autenticación, todos públicos (/auth/**). */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UsuarioRequestDTO dto) {
        AuthResponseDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok().body(authService.login(dto));
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<String> recuperarPassword(@Valid @RequestBody com.daw.dtos.request.RecuperarRequestDTO dto) {
        try {
            authService.solicitarRecuperacion(dto.getEmail());
            return ResponseEntity.ok().body("{\"mensaje\": \"Se ha enviado un código de recuperación a tu email.\"}");
        } catch (com.daw.exceptions.RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"mensaje\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/verificar-codigo")
    public ResponseEntity<String> verificarCodigo(@Valid @RequestBody com.daw.dtos.request.VerificarCodigoRequestDTO dto) {
        boolean valido = authService.verificarCodigo(dto.getEmail(), dto.getCodigo());
        if (valido) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Código válido.\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"mensaje\": \"Código inválido o expirado.\"}");
        }
    }

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
