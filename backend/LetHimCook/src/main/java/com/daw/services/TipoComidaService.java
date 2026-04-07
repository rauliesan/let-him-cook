package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.TipoComidaRequestDTO;
import com.daw.dtos.response.TipoComidaResponseDTO;
import com.daw.entities.TipoComida;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.TipoComidaMapper;
import com.daw.repositories.TipoComidaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de tipos de comida.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TipoComidaService {

    private final TipoComidaRepository tipoComidaRepository;
    private final TipoComidaMapper tipoComidaMapper;

    @Transactional(readOnly = true)
    public List<TipoComidaResponseDTO> listarTodos() {
        return tipoComidaMapper.toListDTO(tipoComidaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TipoComidaResponseDTO buscarPorId(UUID id) {
        TipoComida tipoComida = tipoComidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de Comida no encontrado con ID: " + id));
        return tipoComidaMapper.toResponseDTO(tipoComida);
    }

    public TipoComidaResponseDTO crear(TipoComidaRequestDTO dto) {
        TipoComida tipoComida = tipoComidaMapper.toEntity(dto);
        return tipoComidaMapper.toResponseDTO(tipoComidaRepository.save(tipoComida));
    }

    public TipoComidaResponseDTO actualizar(UUID id, TipoComidaRequestDTO dto) {
        TipoComida tipoComida = tipoComidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de Comida no encontrado con ID: " + id));

        tipoComida.setNombre(dto.getNombre());
        tipoComida.setDescripcion(dto.getDescripcion());
        tipoComida.setIconoUrl(dto.getIconoUrl());

        return tipoComidaMapper.toResponseDTO(tipoComidaRepository.save(tipoComida));
    }

    public void eliminar(UUID id) {
        TipoComida tipoComida = tipoComidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de Comida no encontrado con ID: " + id));
        tipoComidaRepository.delete(tipoComida);
    }
}
