package com.daw.services;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.UsuarioRecompensaRequestDTO;
import com.daw.dtos.response.UsuarioRecompensaResponseDTO;
import com.daw.entities.Recompensa;
import com.daw.entities.Usuario;
import com.daw.entities.UsuarioRecompensa;
import com.daw.exceptions.RecursoDuplicadoException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.UsuarioRecompensaMapper;
import com.daw.repositories.RecompensaRepository;
import com.daw.repositories.UsuarioRecompensaRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioRecompensaService {

    private final UsuarioRecompensaRepository usuarioRecompensaRepository;
    private final RecompensaRepository recompensaRepository;
    private final UsuarioRecompensaMapper usuarioRecompensaMapper;
    private final UsuarioService usuarioService;

    public Page<UsuarioRecompensaResponseDTO> buscarMisRecompensas(UUID usuarioId, Pageable pageable) {
        Page<UsuarioRecompensa> page = usuarioRecompensaRepository.findByUsuarioId(usuarioId, pageable);
        return usuarioRecompensaMapper.toPageDTO(page);
    }

    public UsuarioRecompensaResponseDTO concederRecompensa(UsuarioRecompensaRequestDTO dto, UUID usuarioId) {
        if (usuarioRecompensaRepository.existsByUsuarioIdAndRecompensaId(usuarioId, dto.getRecompensaId())) {
            throw new RecursoDuplicadoException("El usuario ya posee esta recompensa.");
        }

        Recompensa recompensa = recompensaRepository.findById(dto.getRecompensaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Recompensa no encontrada con ID: " + dto.getRecompensaId()));

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioId);

        UsuarioRecompensa usuarioRecompensa = new UsuarioRecompensa();
        usuarioRecompensa.setUsuario(usuario);
        usuarioRecompensa.setRecompensa(recompensa);

        usuarioRecompensa = usuarioRecompensaRepository.save(usuarioRecompensa);
        return usuarioRecompensaMapper.toResponseDTO(usuarioRecompensa);
    }
}
