package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.ApiRequestDTO;
import com.daw.dtos.response.ApiResponseDTO;
import com.daw.entities.Api;
import com.daw.exceptions.RecursoDuplicadoException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.ApiMapper;
import com.daw.repositories.ApiRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de las APIs (gamificación / integración).
 *
 * Proporciona operaciones CRUD completas sobre la entidad {@link Api}.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ApiService {

    private final ApiRepository apiRepository;
    private final ApiMapper apiMapper;

    /**
     * Lista todas las APIs existentes.
     *
     * @return lista de APIs como DTOs de respuesta
     */
    public List<ApiResponseDTO> listarTodos() {
        return apiMapper.toListDTO(apiRepository.findAll());
    }

    /**
     * Busca una API por su ID.
     *
     * @param id identificador de la API
     * @return la API como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe una API con ese ID
     */
    public ApiResponseDTO buscarPorId(UUID id) {
        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("API no encontrada con ID: " + id));
        return apiMapper.toResponseDTO(api);
    }

    /**
     * Crea una nueva API.
     *
     * @param dto datos de la API a crear
     * @return la API creada como DTO de respuesta
     */
    public ApiResponseDTO crear(ApiRequestDTO dto) {
        // REGLA DE NEGOCIO: No permitir URLs de endpoint duplicadas
        if (apiRepository.existsByEndpointUrl(dto.getEndpointUrl())) {
            throw new RecursoDuplicadoException("La URL de endpoint '" + dto.getEndpointUrl() + "' ya está registrada.");
        }

        Api api = apiMapper.toEntity(dto);
        api = apiRepository.save(api);
        return apiMapper.toResponseDTO(api);
    }

    /**
     * Actualiza una API existente.
     *
     * @param id  identificador de la API a actualizar
     * @param dto nuevos datos de la API
     * @return la API actualizada como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe una API con ese ID
     */
    public ApiResponseDTO actualizar(UUID id, ApiRequestDTO dto) {
        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("API no encontrada con ID: " + id));

        // REGLA DE NEGOCIO: No permitir URLs de endpoint duplicadas (si cambia)
        if (!api.getEndpointUrl().equals(dto.getEndpointUrl()) && apiRepository.existsByEndpointUrl(dto.getEndpointUrl())) {
            throw new RecursoDuplicadoException("La URL de endpoint '" + dto.getEndpointUrl() + "' ya está registrada por otra API.");
        }

        api.setNombreServicio(dto.getNombreServicio());
        api.setEndpointUrl(dto.getEndpointUrl());
        api.setApiKey(dto.getApiKey());

        api = apiRepository.save(api);
        return apiMapper.toResponseDTO(api);
    }

    /**
     * Elimina una API por su ID.
     *
     * @param id identificador de la API a eliminar
     * @throws RecursoNoEncontradoException si no existe una API con ese ID
     */
    public void eliminar(UUID id) {
        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("API no encontrada con ID: " + id));
        apiRepository.delete(api);
    }
}
