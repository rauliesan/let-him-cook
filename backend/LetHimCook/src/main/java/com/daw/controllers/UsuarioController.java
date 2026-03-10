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

import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.UsuarioResponseDTO;
import com.daw.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de usuarios.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Lista todos los usuarios existentes.
     *
     * @return 200 OK con la lista de usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(usuarioService.listarTodos());
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return 200 OK con el usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(usuarioService.buscarPorId(id));
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param dto datos del usuario a crear
     * @return 201 Created con el usuario creado
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.registrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id  identificador del usuario a actualizar
     * @param dto nuevos datos del usuario
     * @return 200 OK con el usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok().body(usuarioService.actualizar(id, dto));
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario a eliminar
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
