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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.FavoritoRecetaRequestDTO;
import com.daw.dtos.response.FavoritoRecetaResponseDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.FavoritoRecetaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/favoritos-recetas")
@RequiredArgsConstructor
public class FavoritoRecetaController {

    private final FavoritoRecetaService favoritoRecetaService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<FavoritoRecetaResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(favoritoRecetaService.listarTodos());
    }

    /**
     * Devuelve las recetas favoritas paginadas del usuario autenticado.
     */
    @GetMapping("/mis-favoritos")
    public ResponseEntity<Page<FavoritoRecetaResponseDTO>> buscarMisFavoritos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok().body(favoritoRecetaService.buscarMisFavoritos(usuarioId, pageable));
    }

    /** Devuelve la lista sin paginar de favoritos del usuario autenticado. */
    @GetMapping("/mis-favoritos/todos")
    public ResponseEntity<List<FavoritoRecetaResponseDTO>> getMisFavoritos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(favoritoRecetaService.listarMisFavoritos(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoritoRecetaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(favoritoRecetaService.buscarPorId(id));
    }

    /** Agrega una receta a favoritos. */
    @PostMapping
    public ResponseEntity<FavoritoRecetaResponseDTO> crear(
            @Valid @RequestBody FavoritoRecetaRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(favoritoRecetaService.agregar(dto, usuarioId));
    }

    /** Elimina una receta de favoritos por recetaId (toggle desde el frontend). */
    @DeleteMapping("/{recetaId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable UUID recetaId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        favoritoRecetaService.eliminar(recetaId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
