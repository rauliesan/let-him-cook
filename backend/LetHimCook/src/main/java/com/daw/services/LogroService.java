package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.LogroRequestDTO;
import com.daw.dtos.response.LogroResponseDTO;
import com.daw.entities.Logro;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.LogroMapper;
import com.daw.repositories.LogroRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LogroService {

    private final LogroRepository logroRepository;
    private final LogroMapper logroMapper;

    public List<LogroResponseDTO> listarTodos() {
        return logroRepository.findAll()
                .stream()
                .map(logroMapper::toResponseDTO)
                .toList();
    }

    public LogroResponseDTO buscarPorId(UUID id) {
        return logroRepository.findById(id)
                .map(logroMapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Logro no encontrado con ID: " + id));
    }

    public LogroResponseDTO crear(LogroRequestDTO dto) {
        Logro logro = logroMapper.toEntity(dto);
        logro = logroRepository.save(logro);
        return logroMapper.toResponseDTO(logro);
    }

    public LogroResponseDTO actualizar(UUID id, LogroRequestDTO dto) {
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Logro no encontrado con ID: " + id));

        logro.setNombre(dto.getNombre());
        logro.setDescripcion(dto.getDescripcion());
        logro.setIconoUrl(dto.getIconoUrl());

        logro = logroRepository.save(logro);
        return logroMapper.toResponseDTO(logro);
    }

    public void eliminar(UUID id) {
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Logro no encontrado con ID: " + id));
        logroRepository.delete(logro);
    }
}
