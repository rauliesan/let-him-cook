package com.daw.controllers;

import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.TipoComidaRequestDTO;
import com.daw.dtos.response.TipoComidaResponseDTO;
import com.daw.services.TipoComidaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tipos-comida")
@RequiredArgsConstructor
public class TipoComidaController {

    private final TipoComidaService tipoComidaService;

    @GetMapping
    public ResponseEntity<List<TipoComidaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(tipoComidaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoComidaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(tipoComidaService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TipoComidaResponseDTO> crear(@Valid @RequestBody TipoComidaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoComidaService.crear(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TipoComidaResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody TipoComidaRequestDTO dto) {
        return ResponseEntity.ok(tipoComidaService.actualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        tipoComidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
