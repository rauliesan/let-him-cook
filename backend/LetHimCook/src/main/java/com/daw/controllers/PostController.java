package com.daw.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.PostComentarioRequestDTO;
import com.daw.dtos.request.PostRequestDTO;
import com.daw.dtos.response.PostComentarioResponseDTO;
import com.daw.dtos.response.PostResponseDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Foro de la comunidad: posts y comentarios de foro. */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> listarTodos() {
        return ResponseEntity.ok(postService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> crear(
            @Valid @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.crear(dto, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        postService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<PostComentarioResponseDTO>> listarComentarios(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.listarComentarios(id));
    }

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<PostComentarioResponseDTO> comentar(
            @PathVariable UUID id,
            @Valid @RequestBody PostComentarioRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(postService.comentar(id, dto, usuarioId));
    }

    @DeleteMapping("/{id}/comentarios/{comentarioId}")
    public ResponseEntity<Void> eliminarComentario(
            @PathVariable UUID id,
            @PathVariable UUID comentarioId) {
        postService.eliminarComentario(comentarioId);
        return ResponseEntity.noContent().build();
    }
}
