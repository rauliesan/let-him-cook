package com.daw.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.RecetaRequestDTO;
import com.daw.dtos.response.RecetaResponseDTO;
import com.daw.entities.Dificultad;
import com.daw.security.CustomUserDetails;
import com.daw.services.RecetaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de Recetas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;

    @GetMapping("/todas")
    public ResponseEntity<List<RecetaResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(recetaService.listarTodos());
    }

    @GetMapping
    public ResponseEntity<Page<RecetaResponseDTO>> buscarPaginado(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(required = false) Dificultad dificultad,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(recetaService.buscarPaginado(nombre, dificultad, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(recetaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RecetaResponseDTO> crear(
            @Valid @RequestBody RecetaRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        RecetaResponseDTO response = recetaService.crear(dto, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or @recetaRepository.existsByIdAndUsuarioId(#id, authentication.principal.usuario.id)")
    @PutMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody RecetaRequestDTO dto) {
        return ResponseEntity.ok().body(recetaService.actualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN') or @recetaRepository.existsByIdAndUsuarioId(#id, authentication.principal.usuario.id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        recetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
