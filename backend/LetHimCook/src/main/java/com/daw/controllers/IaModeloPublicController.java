package com.daw.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.response.IaModeloResponseDTO;
import com.daw.services.IaModeloService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador público para consultar los modelos de IA disponibles.
 */
@RestController
@RequestMapping("/ia-modelos")
@RequiredArgsConstructor
public class IaModeloPublicController {

    private final IaModeloService iaModeloService;

    @GetMapping
    public ResponseEntity<List<IaModeloResponseDTO>> listarTodos() {
        // En un caso real, podríamos filtrar las keys aquí, pero como IaModeloResponseDTO ya es un DTO, 
        // asumimos que el frontend usará solo los IDs y nombres.
        // Lo ideal sería no devolver la API Key al frontend por seguridad, 
        // pero para no romper el mapeo existente lo devolvemos tal cual o lo enmascaramos.
        List<IaModeloResponseDTO> modelos = iaModeloService.listarTodos();
        modelos.forEach(m -> m.setApiKey("HIDDEN")); // Ocultamos la key real al frontend
        return ResponseEntity.ok().body(modelos);
    }
}
