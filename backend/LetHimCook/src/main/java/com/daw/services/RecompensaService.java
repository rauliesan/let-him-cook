package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.RecompensaRequestDTO;
import com.daw.dtos.response.RecompensaResponseDTO;
import com.daw.entities.Recompensa;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.RecompensaMapper;
import com.daw.repositories.RecompensaRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;
    private final RecompensaMapper recompensaMapper;

    public List<RecompensaResponseDTO> listarTodos() {
        return recompensaMapper.toListDTO(recompensaRepository.findAll());
    }

    public RecompensaResponseDTO buscarPorId(UUID id) {
        Recompensa recompensa = recompensaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recompensa no encontrada con ID: " + id));
        return recompensaMapper.toResponseDTO(recompensa);
    }

    public RecompensaResponseDTO crear(RecompensaRequestDTO dto) {
        Recompensa recompensa = recompensaMapper.toEntity(dto);
        recompensa = recompensaRepository.save(recompensa);
        return recompensaMapper.toResponseDTO(recompensa);
    }

    public RecompensaResponseDTO actualizar(UUID id, RecompensaRequestDTO dto) {
        Recompensa recompensa = recompensaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recompensa no encontrada con ID: " + id));

        recompensa.setNombre(dto.getNombre());
        recompensa.setDescripcion(dto.getDescripcion());
        recompensa.setEmoji(dto.getEmoji());
        recompensa.setRareza(dto.getRareza());
        recompensa.setProbabilidad(dto.getProbabilidad());

        recompensa = recompensaRepository.save(recompensa);
        return recompensaMapper.toResponseDTO(recompensa);
    }

    public void eliminar(UUID id) {
        Recompensa recompensa = recompensaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recompensa no encontrada con ID: " + id));
        recompensaRepository.delete(recompensa);
    }
}
