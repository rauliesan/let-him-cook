package com.daw.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.daw.dtos.request.RecompensaRequestDTO;
import com.daw.dtos.response.RecompensaResponseDTO;
import com.daw.services.RecompensaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de Recompensas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/recompensas")
@RequiredArgsConstructor
public class RecompensaController {

    private final RecompensaService recompensaService;

    @GetMapping
    public ResponseEntity<List<RecompensaResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(recompensaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecompensaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(recompensaService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RecompensaResponseDTO> crear(@Valid @RequestBody RecompensaRequestDTO dto) {
        RecompensaResponseDTO response = recompensaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RecompensaResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody RecompensaRequestDTO dto) {
        return ResponseEntity.ok().body(recompensaService.actualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        recompensaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
