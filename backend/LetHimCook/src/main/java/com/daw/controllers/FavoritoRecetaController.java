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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.FavoritoRecetaRequestDTO;
import com.daw.dtos.response.FavoritoRecetaResponseDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.FavoritoRecetaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de recetas favoritas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
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
     * Obtiene el listado de recetas favoritas del usuario autenticado de forma
     * paginada.
     *
     * @param userDetails detalles del usuario del token
     * @param pageable    opciones de paginación
     * @return 200 OK con los favoritos
     */
    @GetMapping("/mis-favoritos")
    public ResponseEntity<Page<FavoritoRecetaResponseDTO>> buscarMisFavoritos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok().body(favoritoRecetaService.buscarMisFavoritos(usuarioId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoritoRecetaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(favoritoRecetaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FavoritoRecetaResponseDTO> crear(
            @Valid @RequestBody FavoritoRecetaRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID usuarioId = userDetails.getUsuario().getId();
        FavoritoRecetaResponseDTO response = favoritoRecetaService.crear(dto, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or @favoritoRecetaRepository.existsByIdAndUsuarioId(#id, authentication.principal.usuario.id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        favoritoRecetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
