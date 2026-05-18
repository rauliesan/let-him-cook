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

@Service
@Transactional
@RequiredArgsConstructor
public class ApiService {

    private final ApiRepository apiRepository;
    private final ApiMapper apiMapper;

    public List<ApiResponseDTO> listarTodos() {
        return apiMapper.toListDTO(apiRepository.findAll());
    }

    public ApiResponseDTO buscarPorId(UUID id) {
        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("API no encontrada con ID: " + id));
        return apiMapper.toResponseDTO(api);
    }

    public ApiResponseDTO crear(ApiRequestDTO dto) {
        // no permitir URLs de endpoint duplicadas
        if (apiRepository.existsByEndpointUrl(dto.getEndpointUrl())) {
            throw new RecursoDuplicadoException("La URL de endpoint '" + dto.getEndpointUrl() + "' ya está registrada.");
        }

        Api api = apiMapper.toEntity(dto);
        api = apiRepository.save(api);
        return apiMapper.toResponseDTO(api);
    }

    public ApiResponseDTO actualizar(UUID id, ApiRequestDTO dto) {
        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("API no encontrada con ID: " + id));

        // no permitir URLs de endpoint duplicadas si cambia
        if (!api.getEndpointUrl().equals(dto.getEndpointUrl()) && apiRepository.existsByEndpointUrl(dto.getEndpointUrl())) {
            throw new RecursoDuplicadoException("La URL de endpoint '" + dto.getEndpointUrl() + "' ya está registrada por otra API.");
        }

        api.setNombreServicio(dto.getNombreServicio());
        api.setEndpointUrl(dto.getEndpointUrl());
        api.setApiKey(dto.getApiKey());

        api = apiRepository.save(api);
        return apiMapper.toResponseDTO(api);
    }

    public void eliminar(UUID id) {
        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("API no encontrada con ID: " + id));
        apiRepository.delete(api);
    }
}
