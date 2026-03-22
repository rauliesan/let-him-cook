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

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de recetas favoritas por usuario.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class FavoritoRecetaService {

    private final FavoritoRecetaRepository favoritoRecetaRepository;
    private final RecetaRepository recetaRepository;
    private final FavoritoRecetaMapper favoritoRecetaMapper;
    private final UsuarioService usuarioService;

    /**
     * Lista todas las recetas favoritas existentes (reservado Admin).
     *
     * @return lista de favoritos como DTOs de respuesta
     */
    public List<FavoritoRecetaResponseDTO> listarTodos() {
        return favoritoRecetaMapper.toListDTO(favoritoRecetaRepository.findAll());
    }

    /**
     * Devuelve las recetas favoritas paginadas del usuario logueado.
     *
     * @param usuarioId identificador del usuario extraído del token
     * @param pageable  opciones de paginación
     * @return página de DTOs con los favoritos
     */
    public Page<FavoritoRecetaResponseDTO> buscarMisFavoritos(UUID usuarioId, Pageable pageable) {
        Page<FavoritoReceta> page = favoritoRecetaRepository.findByUsuarioId(usuarioId, pageable);
        return favoritoRecetaMapper.toPageDTO(page);
    }

    /**
     * Busca un favorito por su ID.
     *
     * @param id identificador
     * @return el favorito como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe con ese ID
     */
    public FavoritoRecetaResponseDTO buscarPorId(UUID id) {
        FavoritoReceta favorito = favoritoRecetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Favorito no encontrado con ID: " + id));
        return favoritoRecetaMapper.toResponseDTO(favorito);
    }

    /**
     * Crea un nuevo registro de receta favorita.
     *
     * @param dto       datos de la receta
     * @param usuarioId ID del usuario
     * @return el favorito creado como DTO
     */
    public FavoritoRecetaResponseDTO crear(FavoritoRecetaRequestDTO dto, UUID usuarioId) {
        Receta receta = recetaRepository.findById(dto.getRecetaId())
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + dto.getRecetaId()));

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioId);

        // REGLA DE NEGOCIO: No puedes marcar como favorita tu propia receta
        if (receta.getUsuario().getId().equals(usuarioId)) {
            throw new OperacionInvalidaException("No puedes añadir tu propia receta a favoritos.");
        }

        // Comprobar si ya es favorito
        if (favoritoRecetaRepository.existsByUsuarioIdAndRecetaId(usuarioId, dto.getRecetaId())) {
            throw new RecursoDuplicadoException("Esta receta ya está en tus favoritos.");
        }

        FavoritoReceta favorito = favoritoRecetaMapper.toEntity(dto);
        favorito.setReceta(receta);
        favorito.setUsuario(usuario);

        favorito = favoritoRecetaRepository.save(favorito);
        return favoritoRecetaMapper.toResponseDTO(favorito);
    }

    /**
     * Elimina un favorito por su ID.
     *
     * @param id identificador a eliminar
     * @throws RecursoNoEncontradoException si no existe con ese ID
     */
    public void eliminar(UUID id) {
        FavoritoReceta favorito = favoritoRecetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Favorito no encontrado con ID: " + id));
        favoritoRecetaRepository.delete(favorito);
    }
}
