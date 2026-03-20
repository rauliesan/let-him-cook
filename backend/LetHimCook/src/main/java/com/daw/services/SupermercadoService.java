package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.SupermercadoRequestDTO;
import com.daw.dtos.response.SupermercadoResponseDTO;
import com.daw.entities.Supermercado;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.SupermercadoMapper;
import com.daw.repositories.SupermercadoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de Supermercados.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SupermercadoService {

    private final SupermercadoRepository supermercadoRepository;
    private final SupermercadoMapper supermercadoMapper;

    public List<SupermercadoResponseDTO> listarTodos() {
        return supermercadoMapper.toListDTO(supermercadoRepository.findAll());
    }

    public SupermercadoResponseDTO buscarPorId(UUID id) {
        Supermercado supermercado = supermercadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Supermercado no encontrado con ID: " + id));
        return supermercadoMapper.toResponseDTO(supermercado);
    }

    public Page<SupermercadoResponseDTO> buscarPaginado(String nombre, String direccion, Pageable pageable) {
        Page<Supermercado> page = supermercadoRepository
                .findByNombreContainingIgnoreCaseAndDireccionContainingIgnoreCase(nombre, direccion, pageable);
        return supermercadoMapper.toPageDTO(page);
    }

    public SupermercadoResponseDTO crear(SupermercadoRequestDTO dto) {
        Supermercado supermercado = supermercadoMapper.toEntity(dto);
        supermercado = supermercadoRepository.save(supermercado);
        return supermercadoMapper.toResponseDTO(supermercado);
    }

    public SupermercadoResponseDTO actualizar(UUID id, SupermercadoRequestDTO dto) {
        Supermercado supermercado = supermercadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Supermercado no encontrado con ID: " + id));

        supermercado.setNombre(dto.getNombre());
        supermercado.setDescripcion(dto.getDescripcion());
        supermercado.setValoracion(dto.getValoracion());
        supermercado.setDireccion(dto.getDireccion());
        supermercado.setHorario(dto.getHorario());
        supermercado.setFotoUrl(dto.getFotoUrl());

        supermercado = supermercadoRepository.save(supermercado);
        return supermercadoMapper.toResponseDTO(supermercado);
    }

    public void eliminar(UUID id) {
        Supermercado supermercado = supermercadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Supermercado no encontrado con ID: " + id));
        supermercadoRepository.delete(supermercado);
    }
}
