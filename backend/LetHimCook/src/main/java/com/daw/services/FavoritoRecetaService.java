package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.FavoritoRecetaRequestDTO;
import com.daw.dtos.response.FavoritoRecetaResponseDTO;
import com.daw.entities.FavoritoReceta;
import com.daw.entities.Receta;
import com.daw.entities.Usuario;
import com.daw.exceptions.OperacionInvalidaException;
import com.daw.exceptions.RecursoDuplicadoException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.FavoritoRecetaMapper;
import com.daw.repositories.FavoritoRecetaRepository;
import com.daw.repositories.RecetaRepository;
import com.daw.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoritoRecetaService {

    private final FavoritoRecetaRepository favoritoRecetaRepository;
    private final RecetaRepository         recetaRepository;
    private final UsuarioRepository        usuarioRepository;
    private final FavoritoRecetaMapper     favoritoRecetaMapper;

    /** Lista todos los favoritos (reservado a Admin). */
    public List<FavoritoRecetaResponseDTO> listarTodos() {
        return favoritoRecetaMapper.toListDTO(favoritoRecetaRepository.findAll());
    }

    /** Devuelve las recetas favoritas paginadas del usuario. */
    @Transactional(readOnly = true)
    public Page<FavoritoRecetaResponseDTO> buscarMisFavoritos(UUID usuarioId, Pageable pageable) {
        return favoritoRecetaMapper.toPageDTO(favoritoRecetaRepository.findByUsuarioId(usuarioId, pageable));
    }

    /** Devuelve todas las recetas favoritas del usuario sin paginar. */
    @Transactional(readOnly = true)
    public List<FavoritoRecetaResponseDTO> listarMisFavoritos(UUID usuarioId) {
        return favoritoRecetaMapper.toListDTO(favoritoRecetaRepository.findAllByUsuarioId(usuarioId));
    }

    /** Busca un favorito por su ID. */
    @Transactional(readOnly = true)
    public FavoritoRecetaResponseDTO buscarPorId(UUID id) {
        FavoritoReceta favorito = favoritoRecetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Favorito no encontrado con ID: " + id));
        return favoritoRecetaMapper.toResponseDTO(favorito);
    }

    /**
     * Agrega una receta a favoritos.
     * Si ya estaba guardada devuelve el registro existente (idempotente).
     */
    public FavoritoRecetaResponseDTO agregar(FavoritoRecetaRequestDTO dto, UUID usuarioId) {
        if (favoritoRecetaRepository.existsByUsuarioIdAndRecetaId(usuarioId, dto.getRecetaId())) {
            return favoritoRecetaRepository.findAllByUsuarioId(usuarioId).stream()
                .filter(f -> f.getReceta().getId().equals(dto.getRecetaId()))
                .findFirst()
                .map(favoritoRecetaMapper::toResponseDTO)
                .orElseThrow();
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
        Receta receta = recetaRepository.findById(dto.getRecetaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada."));

        FavoritoReceta favorito = new FavoritoReceta();
        favorito.setUsuario(usuario);
        favorito.setReceta(receta);

        return favoritoRecetaMapper.toResponseDTO(favoritoRecetaRepository.save(favorito));
    }

    /** Elimina una receta de favoritos por recetaId. No lanza error si no existía (toggle seguro). */
    @Transactional
    public void eliminar(UUID recetaId, UUID usuarioId) {
        favoritoRecetaRepository.deleteByUsuarioIdAndRecetaId(usuarioId, recetaId);
    }
}
