package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.ComentarioRequestDTO;
import com.daw.dtos.response.ComentarioResponseDTO;
import com.daw.entities.Comentario;
import com.daw.entities.Receta;
import com.daw.entities.Usuario;
import com.daw.exceptions.OperacionInvalidaException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.ComentarioMapper;
import com.daw.repositories.ComentarioRepository;
import com.daw.repositories.RecetaRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final RecetaRepository recetaRepository;
    private final ComentarioMapper comentarioMapper;
    private final UsuarioService usuarioService;

    public List<ComentarioResponseDTO> listarTodos() {
        return comentarioMapper.toListDTO(comentarioRepository.findAll());
    }

    public Page<ComentarioResponseDTO> buscarPaginadoPorReceta(UUID recetaId, Pageable pageable) {
        Page<Comentario> page = comentarioRepository.findByRecetaId(recetaId, pageable);
        return comentarioMapper.toPageDTO(page);
    }

    public ComentarioResponseDTO buscarPorId(UUID id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comentario no encontrado con ID: " + id));
        return comentarioMapper.toResponseDTO(comentario);
    }

    public ComentarioResponseDTO crear(ComentarioRequestDTO dto, UUID usuarioId) {
        Receta receta = recetaRepository.findById(dto.getRecetaId())
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + dto.getRecetaId()));

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioId);

        // no puedes comentar o valorar tu propia receta
        if (receta.getUsuario().getId().equals(usuarioId)) {
            throw new OperacionInvalidaException("No puedes comentar o valorar tu propia receta.");
        }

        Comentario comentario = comentarioMapper.toEntity(dto);
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);

        comentario = comentarioRepository.save(comentario);
        return comentarioMapper.toResponseDTO(comentario);
    }

    public ComentarioResponseDTO actualizar(UUID id, ComentarioRequestDTO dto) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comentario no encontrado con ID: " + id));

        comentario.setComentario(dto.getComentario());
        comentario.setValoracion(dto.getValoracion());

        comentario = comentarioRepository.save(comentario);
        return comentarioMapper.toResponseDTO(comentario);
    }

    public void eliminar(UUID id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comentario no encontrado con ID: " + id));
        comentarioRepository.delete(comentario);
    }
}
