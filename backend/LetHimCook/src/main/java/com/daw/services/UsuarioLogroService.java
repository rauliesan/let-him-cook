package com.daw.services;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.UsuarioLogroRequestDTO;
import com.daw.dtos.response.UsuarioLogroResponseDTO;
import com.daw.entities.Logro;
import com.daw.entities.Usuario;
import com.daw.entities.UsuarioLogro;
import com.daw.exceptions.RecursoDuplicadoException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.UsuarioLogroMapper;
import com.daw.repositories.LogroRepository;
import com.daw.repositories.UsuarioLogroRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioLogroService {

    private final UsuarioLogroRepository usuarioLogroRepository;
    private final LogroRepository logroRepository;
    private final UsuarioLogroMapper usuarioLogroMapper;
    private final UsuarioService usuarioService;

    public Page<UsuarioLogroResponseDTO> buscarMisLogros(UUID usuarioId, Pageable pageable) {
        Page<UsuarioLogro> page = usuarioLogroRepository.findByUsuarioId(usuarioId, pageable);
        return usuarioLogroMapper.toPageDTO(page);
    }

    public UsuarioLogroResponseDTO concederLogro(UsuarioLogroRequestDTO dto, UUID usuarioId) {
        if (usuarioLogroRepository.existsByUsuarioIdAndLogroId(usuarioId, dto.getLogroId())) {
            throw new RecursoDuplicadoException("El usuario ya posee este logro en su vitrina.");
        }

        Logro logro = logroRepository.findById(dto.getLogroId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Logro no encontrado con ID: " + dto.getLogroId()));

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioId);

        UsuarioLogro usuarioLogro = new UsuarioLogro();
        usuarioLogro.setUsuario(usuario);
        usuarioLogro.setLogro(logro);

        usuarioLogro = usuarioLogroRepository.save(usuarioLogro);
        return usuarioLogroMapper.toResponseDTO(usuarioLogro);
    }
}
