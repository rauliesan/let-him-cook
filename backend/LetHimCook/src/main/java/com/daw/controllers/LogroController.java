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

/**
 * Controlador para la gestión de logros.
 *
 * Todos los endpoints están bajo la ruta /admin/logros, por lo que
 * solo usuarios con rol ADMIN pueden acceder a ellos.
 * 
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/admin/logros")
@RequiredArgsConstructor
public class LogroController {

    private final LogroService logroService;

    /**
     * Lista todos los logros existentes.
     *
     * @return 200 OK con la lista de logros
     */
    @GetMapping
    public ResponseEntity<List<LogroResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(logroService.listarTodos());
    }

    /**
     * Busca un logro por su ID.
     *
     * @param id identificador del logro
     * @return 200 OK con el logro encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<LogroResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(logroService.buscarPorId(id));
    }

    /**
     * Crea un nuevo logro.
     *
     * @param dto datos del logro a crear
     * @return 201 Created con el logro creado
     */
    @PostMapping
    public ResponseEntity<LogroResponseDTO> crear(@Valid @RequestBody LogroRequestDTO dto) {
        LogroResponseDTO response = logroService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un logro existente.
     *
     * @param id  identificador del logro a actualizar
     * @param dto nuevos datos del logro
     * @return 200 OK con el logro actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<LogroResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody LogroRequestDTO dto) {
        return ResponseEntity.ok().body(logroService.actualizar(id, dto));
    }

    /**
     * Elimina un logro por su ID.
     *
     * @param id identificador del logro a eliminar
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        logroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
