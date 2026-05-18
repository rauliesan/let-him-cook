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
import com.daw.services.UsuarioLogroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;
    private final UsuarioLogroService usuarioLogroService;

    /** Lista todas las recetas sin paginar (usado por el frontend de explorar). */
    @GetMapping("/todas")
    public ResponseEntity<List<RecetaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(recetaService.listarTodas());
    }

    /** Recetas creadas por el usuario autenticado. */
    @GetMapping("/mis-recetas")
    public ResponseEntity<List<RecetaResponseDTO>> listarMisRecetas(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(recetaService.listarPorUsuario(usuarioId));
    }

    /** Lista paginada con filtros opcionales por nombre y dificultad. */
    @GetMapping
    public ResponseEntity<Page<RecetaResponseDTO>> buscarPaginado(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(required = false) Dificultad dificultad,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(recetaService.buscarPaginado(nombre, dificultad, pageable));
    }

    /** Búsqueda dinámica en nombre, ingredientes y descripción. */
    @GetMapping("/busqueda")
    public ResponseEntity<Page<RecetaResponseDTO>> buscarDinamico(
            @RequestParam(defaultValue = "") String termino,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) List<UUID> categorias,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok().body(recetaService.buscarDinamico(termino, dificultad, categorias, pageable));
    }

    /** Lista paginada simple (sin filtros). */
    @GetMapping("/pagina")
    public ResponseEntity<Page<RecetaResponseDTO>> listarPaginado(
            @PageableDefault(size = 12, sort = "fechaCreacion") Pageable pageable) {
        return ResponseEntity.ok(recetaService.listarPaginado(pageable));
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
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaService.crear(dto, usuarioId));
    }

    @PreAuthorize("hasRole('ADMIN') or @recetaRepository.existsByIdAndUsuarioId(#id, authentication.principal.usuario.id)")
    @PutMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody RecetaRequestDTO dto) {
        return ResponseEntity.ok().body(recetaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        recetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Recetas públicas de los amigos del usuario autenticado. */
    @GetMapping("/amigos")
    public ResponseEntity<List<RecetaResponseDTO>> recetasDeAmigos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(recetaService.listarRecetasDeAmigos(userDetails.getUsuario().getId()));
    }

    /** Recetas públicas de un usuario concreto (para perfiles públicos). */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<RecetaResponseDTO>> recetasPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(recetaService.listarPublicasPorUsuario(usuarioId));
    }

    /** Registra la receta como cocinada y devuelve monedas ganadas + logros nuevos en un solo paso. */
    @PostMapping("/{id}/completar")
    public ResponseEntity<CompletarRecetaResponse> completarReceta(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        int monedas = recetaService.completarReceta(usuarioId, id);
        List<String> logrosNuevos = monedas != -1
                ? usuarioLogroService.verificarLogros(usuarioId)
                : List.of();
        return ResponseEntity.ok(new CompletarRecetaResponse(monedas, logrosNuevos));
    }

    record CompletarRecetaResponse(int monedas, List<String> logrosNuevos) {}

    /** Comprueba si el usuario autenticado ya completó esta receta. */
    @GetMapping("/{id}/completada")
    public ResponseEntity<Boolean> haCompletado(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(recetaService.haCompletado(usuarioId, id));
    }
}
