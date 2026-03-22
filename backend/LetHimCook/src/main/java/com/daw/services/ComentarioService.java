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

/**
 * Servicio para la gestión de comentarios de recetas.
 *
 * Proporciona operaciones CRUD completas sobre la entidad {@link Comentario}.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final RecetaRepository recetaRepository;
    private final ComentarioMapper comentarioMapper;
    private final UsuarioService usuarioService;

    /**
     * Lista todos los comentarios existentes.
     *
     * @return lista de comentarios como DTOs de respuesta
     */
    public List<ComentarioResponseDTO> listarTodos() {
        return comentarioMapper.toListDTO(comentarioRepository.findAll());
    }

    /**
     * Devuelve una página de comentarios dada la ID de una Receta.
     *
     * @param recetaId identificador de la receta a la que pertenecen
     * @param pageable configuración de paginación
     * @return página de respuesta de DTOs
     */
    public Page<ComentarioResponseDTO> buscarPaginadoPorReceta(UUID recetaId, Pageable pageable) {
        Page<Comentario> page = comentarioRepository.findByRecetaId(recetaId, pageable);
        return comentarioMapper.toPageDTO(page);
    }

    /**
     * Busca un comentario por su ID.
     *
     * @param id identificador del comentario
     * @return el comentario como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe un comentario con ese ID
     */
    public ComentarioResponseDTO buscarPorId(UUID id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comentario no encontrado con ID: " + id));
        return comentarioMapper.toResponseDTO(comentario);
    }

    /**
     * Crea un nuevo comentario.
     *
     * @param dto       datos del comentario a crear
     * @param usuarioId ID del usuario que crea el comentario extraído del Token
     * @return el comentario creado como DTO de respuesta
     * @throws RecursoNoEncontradoException si la receta no existe
     */
    public ComentarioResponseDTO crear(ComentarioRequestDTO dto, UUID usuarioId) {
        Receta receta = recetaRepository.findById(dto.getRecetaId())
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + dto.getRecetaId()));

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioId);

        // REGLA DE NEGOCIO: No puedes comentar/valorar tu propia receta
        if (receta.getUsuario().getId().equals(usuarioId)) {
            throw new OperacionInvalidaException("No puedes comentar o valorar tu propia receta.");
        }

        Comentario comentario = comentarioMapper.toEntity(dto);
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);

        comentario = comentarioRepository.save(comentario);
        return comentarioMapper.toResponseDTO(comentario);
    }

    /**
     * Actualiza un comentario existente.
     * 
     * @param id  identificador del comentario a actualizar
     * @param dto nuevos datos del comentario
     * @return el comentario actualizado como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe el comentario a editar
     */
    public ComentarioResponseDTO actualizar(UUID id, ComentarioRequestDTO dto) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comentario no encontrado con ID: " + id));

        comentario.setComentario(dto.getComentario());
        comentario.setValoracion(dto.getValoracion());

        comentario = comentarioRepository.save(comentario);
        return comentarioMapper.toResponseDTO(comentario);
    }

    /**
     * Elimina un comentario por su ID.
     *
     * @param id identificador del comentario a eliminar
     * @throws RecursoNoEncontradoException si no existe un comentario con ese ID
     */
    public void eliminar(UUID id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comentario no encontrado con ID: " + id));
        comentarioRepository.delete(comentario);
    }
}
