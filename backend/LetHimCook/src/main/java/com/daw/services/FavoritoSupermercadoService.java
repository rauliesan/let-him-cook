package com.daw.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.FavoritoSupermercadoRequestDTO;
import com.daw.dtos.response.FavoritoSupermercadoResponseDTO;
import com.daw.entities.FavoritoSupermercado;
import com.daw.entities.Supermercado;
import com.daw.entities.Usuario;

import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.FavoritoSupermercadoMapper;
import com.daw.repositories.FavoritoSupermercadoRepository;
import com.daw.repositories.SupermercadoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de supermercados favoritos.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class FavoritoSupermercadoService {

    private final FavoritoSupermercadoRepository favoritoSupermercadoRepository;
    private final SupermercadoRepository supermercadoRepository;
    private final FavoritoSupermercadoMapper favoritoSupermercadoMapper;
    private final UsuarioService usuarioService;

    public List<FavoritoSupermercadoResponseDTO> listarTodos() {
        return favoritoSupermercadoMapper.toListDTO(favoritoSupermercadoRepository.findAll());
    }

    /**
     * Devuelve los supermercados favoritos paginados del usuario logueado.
     *
     * @param usuarioId identificador del usuario extraído del token
     * @param pageable  opciones de paginación
     * @return página de DTOs con los favoritos
     */
    public Page<FavoritoSupermercadoResponseDTO> buscarMisFavoritos(UUID usuarioId, Pageable pageable) {
        Page<FavoritoSupermercado> page = favoritoSupermercadoRepository.findByUsuarioId(usuarioId, pageable);
        return favoritoSupermercadoMapper.toPageDTO(page);
    }

    public FavoritoSupermercadoResponseDTO buscarPorId(UUID id) {
        FavoritoSupermercado favorito = favoritoSupermercadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Favorito no encontrado con ID: " + id));
        return favoritoSupermercadoMapper.toResponseDTO(favorito);
    }

    public FavoritoSupermercadoResponseDTO crear(FavoritoSupermercadoRequestDTO dto, UUID usuarioId) {
        Supermercado supermercado = supermercadoRepository.findById(dto.getSupermercadoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Supermercado no encontrado con ID: " + dto.getSupermercadoId()));

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioId);

        FavoritoSupermercado favorito = favoritoSupermercadoMapper.toEntity(dto);
        favorito.setSupermercado(supermercado);
        favorito.setUsuario(usuario);

        favorito = favoritoSupermercadoRepository.save(favorito);
        return favoritoSupermercadoMapper.toResponseDTO(favorito);
    }

    public void eliminar(UUID id) {
        FavoritoSupermercado favorito = favoritoSupermercadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Favorito no encontrado con ID: " + id));
        favoritoSupermercadoRepository.delete(favorito);
    }
}
