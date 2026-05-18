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

import com.daw.dtos.request.ApiRequestDTO;
import com.daw.dtos.response.ApiResponseDTO;
import com.daw.services.ApiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/apis")
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;

    /**
     * Lista todas las APIs existentes.
     *
     * @return 200 OK con la lista de APIs
     */
    @GetMapping
    public ResponseEntity<List<ApiResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(apiService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(apiService.buscarPorId(id));
    }

    /**
     * Crea una nueva API.
     *
     * @param dto datos de la API a crear
     * @return 201 Created con la API creada
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO> crear(@Valid @RequestBody ApiRequestDTO dto) {
        ApiResponseDTO response = apiService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody ApiRequestDTO dto) {
        return ResponseEntity.ok().body(apiService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        apiService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
