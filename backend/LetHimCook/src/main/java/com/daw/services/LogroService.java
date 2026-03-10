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

/**
 * Servicio para la gestión de logros.
 *
 * Proporciona operaciones CRUD completas sobre la entidad {@link Logro}.
 * Estas operaciones están pensadas para ser consumidas por el panel de
 * administración (solo ADMIN).
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LogroService {

    private final LogroRepository logroRepository;
    private final LogroMapper logroMapper;

    /**
     * Lista todos los logros existentes.
     *
     * @return lista de logros como DTOs de respuesta
     */
    
    public List<LogroResponseDTO> listarTodos() {
        return logroRepository.findAll()
                .stream()
                .map(logroMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca un logro por su ID.
     *
     * @param id identificador del logro
     * @return el logro como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe un logro con ese ID
     */
    public LogroResponseDTO buscarPorId(UUID id) {
        return logroRepository.findById(id)
                .map(logroMapper::toResponseDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Logro no encontrado con ID: " + id));
    }

    /**
     * Crea un nuevo logro.
     *
     * @param dto datos del logro a crear
     * @return el logro creado como DTO de respuesta
     */
    public LogroResponseDTO crear(LogroRequestDTO dto) {
        Logro logro = logroMapper.toEntity(dto);
        logro = logroRepository.save(logro);
        return logroMapper.toResponseDTO(logro);
    }

    /**
     * Actualiza un logro existente.
     *
     * @param id  identificador del logro a actualizar
     * @param dto nuevos datos del logro
     * @return el logro actualizado como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe un logro con ese ID
     */
    public LogroResponseDTO actualizar(UUID id, LogroRequestDTO dto) {
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Logro no encontrado con ID: " + id));

        logro.setNombre(dto.getNombre());
        logro.setDescripcion(dto.getDescripcion());
        logro.setIconoUrl(dto.getIconoUrl());

        logro = logroRepository.save(logro);
        return logroMapper.toResponseDTO(logro);
    }

    /**
     * Elimina un logro por su ID.
     *
     * @param id identificador del logro a eliminar
     * @throws RecursoNoEncontradoException si no existe un logro con ese ID
     */
    public void eliminar(UUID id) {
        Logro logro = logroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Logro no encontrado con ID: " + id));
        logroRepository.delete(logro);
    }
}
