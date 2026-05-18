package com.daw.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.response.IaModeloResponseDTO;
import com.daw.services.IaModeloService;

import lombok.RequiredArgsConstructor;

/** Endpoint público para listar los modelos de IA disponibles (oculta la API key). */
@RestController
@RequestMapping("/ia-modelos")
@RequiredArgsConstructor
public class IaModeloPublicController {

    private final IaModeloService iaModeloService;

    @GetMapping
    public ResponseEntity<List<IaModeloResponseDTO>> listarTodos() {
        List<IaModeloResponseDTO> modelos = iaModeloService.listarTodos();
        modelos.forEach(m -> m.setApiKey("HIDDEN")); // no exponer la key al frontend
        return ResponseEntity.ok().body(modelos);
    }
}
