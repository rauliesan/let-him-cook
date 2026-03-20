package com.daw.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.SupermercadoRequestDTO;
import com.daw.dtos.response.SupermercadoResponseDTO;
import com.daw.services.SupermercadoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de Supermercados.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/supermercados")
@RequiredArgsConstructor
public class SupermercadoController {

    private final SupermercadoService supermercadoService;

    @GetMapping("/todos")
    public ResponseEntity<List<SupermercadoResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(supermercadoService.listarTodos());
    }

    @GetMapping
    public ResponseEntity<Page<SupermercadoResponseDTO>> buscarPaginado(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "") String direccion,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(supermercadoService.buscarPaginado(nombre, direccion, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupermercadoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(supermercadoService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SupermercadoResponseDTO> crear(@Valid @RequestBody SupermercadoRequestDTO dto) {
        SupermercadoResponseDTO response = supermercadoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SupermercadoResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody SupermercadoRequestDTO dto) {
        return ResponseEntity.ok().body(supermercadoService.actualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        supermercadoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
