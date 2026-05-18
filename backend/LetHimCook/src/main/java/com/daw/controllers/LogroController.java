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

import com.daw.dtos.request.LogroRequestDTO;
import com.daw.dtos.response.LogroResponseDTO;
import com.daw.services.LogroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/logros")
@RequiredArgsConstructor
public class LogroController {

    private final LogroService logroService;

    @GetMapping
    public ResponseEntity<List<LogroResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(logroService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogroResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(logroService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<LogroResponseDTO> crear(@Valid @RequestBody LogroRequestDTO dto) {
        LogroResponseDTO response = logroService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LogroResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody LogroRequestDTO dto) {
        return ResponseEntity.ok().body(logroService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        logroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
