package com.daw.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.ComentarioRequestDTO;
import com.daw.dtos.response.ComentarioResponseDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.ComentarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @GetMapping
    public ResponseEntity<List<ComentarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(comentarioService.listarTodos());
    }

    @GetMapping("/receta/{recetaId}")
    public ResponseEntity<Page<ComentarioResponseDTO>> buscarPorReceta(
            @PathVariable UUID recetaId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(comentarioService.buscarPaginadoPorReceta(recetaId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(comentarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> crear(
            @Valid @RequestBody ComentarioRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID usuarioId = userDetails.getUsuario().getId();
        ComentarioResponseDTO response = comentarioService.crear(dto, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or @comentarioRepository.existsByIdAndUsuarioId(#id, authentication.principal.usuario.id)")
    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody ComentarioRequestDTO dto) {
        return ResponseEntity.ok().body(comentarioService.actualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN') or @comentarioRepository.existsByIdAndUsuarioId(#id, authentication.principal.usuario.id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        comentarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
