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

import com.daw.dtos.request.IaModeloRequestDTO;
import com.daw.dtos.response.IaModeloResponseDTO;
import com.daw.services.IaModeloService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/ia-modelos")
@RequiredArgsConstructor
public class IaModeloController {

    private final IaModeloService iaModeloService;

    @GetMapping
    public ResponseEntity<List<IaModeloResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(iaModeloService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IaModeloResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(iaModeloService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<IaModeloResponseDTO> crear(@Valid @RequestBody IaModeloRequestDTO dto) {
        IaModeloResponseDTO response = iaModeloService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IaModeloResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody IaModeloRequestDTO dto) {
        return ResponseEntity.ok().body(iaModeloService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        iaModeloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
