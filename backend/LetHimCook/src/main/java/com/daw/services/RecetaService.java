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
import com.daw.entities.IaModelo;
import com.daw.entities.Receta;
import com.daw.entities.TipoComida;
import com.daw.entities.Usuario;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.RecetaMapper;
import com.daw.repositories.IaModeloRepository;
import com.daw.repositories.RecetaRepository;
import com.daw.repositories.TipoComidaRepository;
import com.daw.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de recetas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RecetaService {

    private final RecetaRepository      recetaRepository;
    private final TipoComidaRepository  tipoComidaRepository;
    private final IaModeloRepository    iaModeloRepository;
    private final UsuarioRepository     usuarioRepository;
    private final RecetaMapper          recetaMapper;

    /** Lista todas las recetas (sin paginar — usado por el frontend). */
    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listarTodas() {
        return recetaMapper.toListDTO(recetaRepository.findAll());
    }

    /** Recetas creadas por un usuario concreto, ordenadas por fecha descendente. */
    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listarPorUsuario(UUID usuarioId) {
        return recetaMapper.toListDTO(recetaRepository.findByUsuario_IdOrderByFechaCreacionDesc(usuarioId));
    }

    /** Lista paginada con filtros opcionales. */
    @Transactional(readOnly = true)
    public Page<RecetaResponseDTO> buscarPaginado(String nombre, Dificultad dificultad, Pageable pageable) {
        Page<Receta> page = recetaRepository.findByNombreContainingIgnoreCaseAndDificultad(nombre, dificultad, pageable);
        return recetaMapper.toPageDTO(page);
    }

    /** Búsqueda dinámica en múltiples campos. */
    @Transactional(readOnly = true)
    public Page<RecetaResponseDTO> buscarDinamico(String termino, Dificultad dificultad, List<UUID> categorias, Pageable pageable) {
        Page<Receta> page = recetaRepository.buscarDinamico(termino, dificultad, categorias, pageable);
        return recetaMapper.toPageDTO(page);
    }

    /** Lista paginada simple sin filtros. */
    @Transactional(readOnly = true)
    public Page<RecetaResponseDTO> listarPaginado(Pageable pageable) {
        return recetaMapper.toPageDTO(recetaRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public RecetaResponseDTO buscarPorId(UUID id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        return recetaMapper.toResponseDTO(receta);
    }

    public RecetaResponseDTO crear(RecetaRequestDTO dto, UUID usuarioCreadorId) {
        Usuario usuario = usuarioRepository.findById(usuarioCreadorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));

        Receta receta = recetaMapper.toEntity(dto);
        receta.setUsuario(usuario);
        receta.setFechaCreacion(ZonedDateTime.now(ZoneId.of("Europe/Madrid")));

        if (dto.getTipoComidaId() != null) {
            TipoComida tipoComida = tipoComidaRepository.findById(dto.getTipoComidaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Tipo de Comida no encontrado con ID: " + dto.getTipoComidaId()));
            receta.setTipoComida(tipoComida);
        }

        if (dto.getIaModeloId() != null) {
            IaModelo iaModelo = iaModeloRepository.findById(dto.getIaModeloId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Modelo de IA no encontrado con ID: " + dto.getIaModeloId()));
            receta.setIaModelo(iaModelo);
        }

        return recetaMapper.toResponseDTO(recetaRepository.save(receta));
    }

    public RecetaResponseDTO actualizar(UUID id, RecetaRequestDTO dto) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));

        if (dto.getTipoComidaId() != null) {
            TipoComida tipoComida = tipoComidaRepository.findById(dto.getTipoComidaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Tipo de Comida no encontrado con ID: " + dto.getTipoComidaId()));
            receta.setTipoComida(tipoComida);
        }

        if (dto.getIaModeloId() != null) {
            IaModelo iaModelo = iaModeloRepository.findById(dto.getIaModeloId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Modelo de IA no encontrado con ID: " + dto.getIaModeloId()));
            receta.setIaModelo(iaModelo);
        } else {
            receta.setIaModelo(null);
        }

        receta.setNombre(dto.getNombre());
        receta.setDescripcion(dto.getDescripcion());
        receta.setIngredientes(dto.getIngredientes());
        receta.setTiempoPreparacion(dto.getTiempoPreparacion());
        receta.setDificultad(dto.getDificultad());
        receta.setCalorias(dto.getCalorias());
        receta.setAlergenos(dto.getAlergenos());
        receta.setEsPublica(dto.getEsPublica());
        receta.setImagenUrl(dto.getImagenUrl());

        return recetaMapper.toResponseDTO(recetaRepository.save(receta));
    }

    public void eliminar(UUID id) {
        if (!recetaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Receta no encontrada con ID: " + id);
        }
        recetaRepository.deleteById(id);
    }

    /**
     * Registra que se ha completado una receta y otorga monedas al usuario.
     * Recompensa: 50 monedas. Máximo diario: 150 monedas.
     */
    public int completarReceta(UUID usuarioId, UUID recetaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        java.time.LocalDate hoy = java.time.LocalDate.now(java.time.ZoneId.of("Europe/Madrid"));

        // Resetear contador diario si es un nuevo día
        if (usuario.getFechaUltimaRecetaRecompensa() == null || !usuario.getFechaUltimaRecetaRecompensa().equals(hoy)) {
            usuario.setPuntosRecetaHoy(0);
            usuario.setFechaUltimaRecetaRecompensa(hoy);
        }

        // Comprobar límite diario (150 monedas = 3 recetas)
        int ganancia = 0;
        if (usuario.getPuntosRecetaHoy() < 150) {
            ganancia = 50;
            usuario.setPuntos(usuario.getPuntos() + ganancia);
            usuario.setPuntosRecetaHoy(usuario.getPuntosRecetaHoy() + ganancia);
            usuarioRepository.save(usuario);
        }

        return ganancia;
    }
}
