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

import com.daw.dtos.request.UsuarioLogroRequestDTO;
import com.daw.dtos.response.UsuarioLogroResponseDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.UsuarioLogroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mis-logros")
@RequiredArgsConstructor
public class UsuarioLogroController {

    private final UsuarioLogroService usuarioLogroService;

    @GetMapping
    public ResponseEntity<Page<UsuarioLogroResponseDTO>> buscarMisLogros(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok().body(usuarioLogroService.buscarMisLogros(usuarioId, pageable));
    }

    @PostMapping
    public ResponseEntity<UsuarioLogroResponseDTO> concederLogro(
            @Valid @RequestBody UsuarioLogroRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID usuarioId = userDetails.getUsuario().getId();
        UsuarioLogroResponseDTO response = usuarioLogroService.concederLogro(dto, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Verifica y concede todos los logros que el usuario haya desbloqueado. */
    @PostMapping("/verificar")
    public ResponseEntity<Void> verificarLogros(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        usuarioLogroService.verificarLogros(userDetails.getUsuario().getId());
        return ResponseEntity.ok().build();
    }
}
