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

import com.daw.dtos.request.FavoritoSupermercadoRequestDTO;
import com.daw.dtos.response.FavoritoSupermercadoResponseDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.FavoritoSupermercadoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de supermercados favoritos.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/favoritos-supermercados")
@RequiredArgsConstructor
public class FavoritoSupermercadoController {

    private final FavoritoSupermercadoService favoritoSupermercadoService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<FavoritoSupermercadoResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(favoritoSupermercadoService.listarTodos());
    }

    /**
     * Obtiene el listado de supermercados favoritos del usuario autenticado de
     * forma paginada.
     *
     * @param userDetails detalles del usuario del token
     * @param pageable    opciones de paginación
     * @return 200 OK con los favoritos
     */
    @GetMapping("/mis-favoritos")
    public ResponseEntity<Page<FavoritoSupermercadoResponseDTO>> buscarMisFavoritos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok().body(favoritoSupermercadoService.buscarMisFavoritos(usuarioId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoritoSupermercadoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(favoritoSupermercadoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FavoritoSupermercadoResponseDTO> crear(
            @Valid @RequestBody FavoritoSupermercadoRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UUID usuarioId = userDetails.getUsuario().getId();
        FavoritoSupermercadoResponseDTO response = favoritoSupermercadoService.crear(dto, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN') or @favoritoSupermercadoRepository.existsByIdAndUsuarioId(#id, authentication.principal.usuario.id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        favoritoSupermercadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
