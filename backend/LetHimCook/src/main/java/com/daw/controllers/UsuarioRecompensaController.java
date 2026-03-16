package com.daw.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.UsuarioRecompensaRequestDTO;
import com.daw.dtos.response.UsuarioRecompensaResponseDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.UsuarioRecompensaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de las recompensas ganadas por el usuario.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/mis-recompensas")
@RequiredArgsConstructor
public class UsuarioRecompensaController {

    private final UsuarioRecompensaService usuarioRecompensaService;

    @GetMapping
    public ResponseEntity<Page<UsuarioRecompensaResponseDTO>> buscarMisRecompensas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok().body(usuarioRecompensaService.buscarMisRecompensas(usuarioId, pageable));
    }

    @PostMapping
    public ResponseEntity<UsuarioRecompensaResponseDTO> concederRecompensa(
            @Valid @RequestBody UsuarioRecompensaRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID usuarioId = userDetails.getUsuario().getId();
        UsuarioRecompensaResponseDTO response = usuarioRecompensaService.concederRecompensa(dto, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
