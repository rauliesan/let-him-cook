package com.daw.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.RecetaRequestDTO;
import com.daw.dtos.response.RecetaResponseDTO;
import com.daw.entities.Dificultad;
import com.daw.entities.Receta;
import com.daw.entities.TipoComida;
import com.daw.entities.Usuario;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.RecetaMapper;
import com.daw.repositories.RecetaRepository;
import com.daw.repositories.TipoComidaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de Recetas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final RecetaMapper recetaMapper;
    private final UsuarioService usuarioService;

    public List<RecetaResponseDTO> listarTodos() {
        return recetaMapper.toListDTO(recetaRepository.findAll());
    }

    public RecetaResponseDTO buscarPorId(UUID id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        return recetaMapper.toResponseDTO(receta);
    }

    public Page<RecetaResponseDTO> buscarPaginado(String nombre, Dificultad dificultad, Pageable pageable) {
        Page<Receta> page = recetaRepository.findByNombreContainingIgnoreCaseAndDificultad(nombre, dificultad,
                pageable);
        return recetaMapper.toPageDTO(page);
    }

    public RecetaResponseDTO crear(RecetaRequestDTO dto, UUID usuarioCreadorId) {
        TipoComida tipoComida = tipoComidaRepository.findById(dto.getTipoComidaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tipo de Comida no encontrado con ID: " + dto.getTipoComidaId()));

        Usuario usuario = usuarioService.buscarEntidadPorId(usuarioCreadorId);

        Receta receta = recetaMapper.toEntity(dto);
        receta.setTipoComida(tipoComida);
        receta.setUsuario(usuario);
        receta.setFechaCreacion(ZonedDateTime.now(ZoneId.of("Europe/Madrid")));

        receta = recetaRepository.save(receta);
        return recetaMapper.toResponseDTO(receta);
    }

    public RecetaResponseDTO actualizar(UUID id, RecetaRequestDTO dto) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));

        TipoComida tipoComida = tipoComidaRepository.findById(dto.getTipoComidaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tipo de Comida no encontrado con ID: " + dto.getTipoComidaId()));

        receta.setNombre(dto.getNombre());
        receta.setDescripcion(dto.getDescripcion());
        receta.setIngredientes(dto.getIngredientes());
        receta.setTiempoPreparacion(dto.getTiempoPreparacion());
        receta.setDificultad(dto.getDificultad());
        receta.setCalorias(dto.getCalorias());
        receta.setAlergenos(dto.getAlergenos());
        receta.setEsPublica(dto.getEsPublica());
        receta.setImagenUrl(dto.getImagenUrl());
        receta.setTipoComida(tipoComida);

        receta = recetaRepository.save(receta);
        return recetaMapper.toResponseDTO(receta);
    }

    public void eliminar(UUID id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        recetaRepository.delete(receta);
    }
}
