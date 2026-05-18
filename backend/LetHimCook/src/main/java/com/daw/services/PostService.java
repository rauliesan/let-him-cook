package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.PostComentarioRequestDTO;
import com.daw.dtos.request.PostRequestDTO;
import com.daw.dtos.response.PostComentarioResponseDTO;
import com.daw.dtos.response.PostResponseDTO;
import com.daw.entities.Post;
import com.daw.entities.PostComentario;
import com.daw.entities.Receta;
import com.daw.entities.Usuario;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.PostComentarioMapper;
import com.daw.mappers.PostMapper;
import com.daw.repositories.PostComentarioRepository;
import com.daw.repositories.PostRepository;
import com.daw.repositories.RecetaRepository;
import com.daw.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para el foro: posts y sus comentarios.
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final PostRepository            postRepository;
    private final PostComentarioRepository  comentarioRepository;
    private final UsuarioRepository         usuarioRepository;
    private final RecetaRepository          recetaRepository;
    private final PostMapper                postMapper;
    private final PostComentarioMapper      comentarioMapper;

    /* Devuelve todos los posts ordenados por fecha descendente */
    @Transactional(readOnly = true)
    public List<PostResponseDTO> listarTodos() {
        List<Post> posts = postRepository.findAll();
        posts.sort((a, b) -> b.getFechaCreacion().compareTo(a.getFechaCreacion()));
        return postMapper.toListDTO(posts);
    }

    /* Devuelve un post por ID */
    @Transactional(readOnly = true)
    public PostResponseDTO buscarPorId(UUID id) {
        return postMapper.toResponseDTO(
            postRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Post no encontrado."))
        );
    }

    /* Crea un nuevo post */
    public PostResponseDTO crear(PostRequestDTO dto, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));

        Post post = postMapper.toEntity(dto);
        post.setUsuario(usuario);

        if (dto.getRecetaVinculadaId() != null) {
            Receta receta = recetaRepository.findById(dto.getRecetaVinculadaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada."));
            post.setRecetaVinculada(receta);
        }

        return postMapper.toResponseDTO(postRepository.save(post));
    }

    /* Elimina un post (solo lo puede hacer su autor, validación en el controller) */
    public void eliminar(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new RecursoNoEncontradoException("Post no encontrado.");
        }
        postRepository.deleteById(postId);
    }

    /* Devuelve los comentarios de un post */
    @Transactional(readOnly = true)
    public List<PostComentarioResponseDTO> listarComentarios(UUID postId) {
        return comentarioMapper.toListDTO(
            comentarioRepository.findAllByPostIdOrderByFechaCreacionAsc(postId)
        );
    }

    /* Agrega un comentario a un post */
    public PostComentarioResponseDTO comentar(UUID postId, PostComentarioRequestDTO dto, UUID usuarioId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Post no encontrado."));
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));

        PostComentario comentario = comentarioMapper.toEntity(dto);
        comentario.setPost(post);
        comentario.setUsuario(usuario);

        if (dto.getRecetaVinculadaId() != null) {
            recetaRepository.findById(dto.getRecetaVinculadaId())
                .ifPresent(comentario::setRecetaVinculada);
        }

        return comentarioMapper.toResponseDTO(comentarioRepository.save(comentario));
    }

    /* Elimina un comentario */
    public void eliminarComentario(UUID comentarioId) {
        if (!comentarioRepository.existsById(comentarioId)) {
            throw new RecursoNoEncontradoException("Comentario no encontrado.");
        }
        comentarioRepository.deleteById(comentarioId);
    }
}
