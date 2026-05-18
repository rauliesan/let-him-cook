package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.IaModeloRequestDTO;
import com.daw.dtos.response.IaModeloResponseDTO;
import com.daw.entities.Api;
import com.daw.entities.IaModelo;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.IaModeloMapper;
import com.daw.repositories.ApiRepository;
import com.daw.repositories.IaModeloRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class IaModeloService {

    private final IaModeloRepository iaModeloRepository;
    private final ApiRepository apiRepository;
    private final IaModeloMapper iaModeloMapper;

    public List<IaModeloResponseDTO> listarTodos() {
        return iaModeloMapper.toListDTO(iaModeloRepository.findAll());
    }

    public IaModeloResponseDTO buscarPorId(UUID id) {
        IaModelo modelo = iaModeloRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Modelo de IA no encontrado con ID: " + id));
        return iaModeloMapper.toResponseDTO(modelo);
    }

    public IaModeloResponseDTO crear(IaModeloRequestDTO dto) {
        Api api = apiRepository.findById(dto.getApiId())
                .orElseThrow(() -> new RecursoNoEncontradoException("API no encontrada con ID: " + dto.getApiId()));

        IaModelo modelo = iaModeloMapper.toEntity(dto);
        modelo.setApi(api);

        modelo = iaModeloRepository.save(modelo);
        return iaModeloMapper.toResponseDTO(modelo);
    }

    public IaModeloResponseDTO actualizar(UUID id, IaModeloRequestDTO dto) {
        IaModelo modelo = iaModeloRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Modelo de IA no encontrado con ID: " + id));

        Api api = apiRepository.findById(dto.getApiId())
                .orElseThrow(() -> new RecursoNoEncontradoException("API no encontrada con ID: " + dto.getApiId()));

        modelo.setNombreModelo(dto.getNombreModelo());
        modelo.setApi(api);

        modelo = iaModeloRepository.save(modelo);
        return iaModeloMapper.toResponseDTO(modelo);
    }

    public void eliminar(UUID id) {
        IaModelo modelo = iaModeloRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Modelo de IA no encontrado con ID: " + id));
        iaModeloRepository.delete(modelo);
    }
}
