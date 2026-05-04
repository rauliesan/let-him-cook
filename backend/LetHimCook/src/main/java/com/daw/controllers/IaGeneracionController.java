package com.daw.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.GenerarRecetasRequestDTO;
import com.daw.dtos.request.PublicarRecetaIaRequestDTO;
import com.daw.dtos.response.PublicarRecetaResponseDTO;
import com.daw.dtos.response.RecetaSugerenciaDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.IaGeneracionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la generación de recetas mediante IA y su publicación.
 */
@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
public class IaGeneracionController {

    private final IaGeneracionService iaGeneracionService;

    /**
     * Genera 3 sugerencias de recetas a partir de los ingredientes dados.
     * Usa el modelo de IA configurado en el perfil del usuario o una clave propia (BYOAI).
     */
    @PostMapping("/generar")
    public ResponseEntity<List<RecetaSugerenciaDTO>> generar(
            @Valid @RequestBody GenerarRecetasRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.ok(iaGeneracionService.generarSugerencias(dto, usuarioId));
    }

    /**
     * Publica la receta generada por IA. Crea la categoría si no existe y suma puntos al usuario.
     */
    @PostMapping("/publicar")
    public ResponseEntity<PublicarRecetaResponseDTO> publicar(
            @Valid @RequestBody PublicarRecetaIaRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID usuarioId = userDetails.getUsuario().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iaGeneracionService.publicar(dto, usuarioId));
    }
}
