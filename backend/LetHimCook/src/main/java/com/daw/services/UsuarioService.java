package com.daw.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.UsuarioResponseDTO;
import com.daw.entities.IaModelo;
import com.daw.entities.Rol;
import com.daw.entities.Usuario;
import com.daw.exceptions.RecursoDuplicadoException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.UsuarioMapper;
import com.daw.repositories.IaModeloRepository;
import com.daw.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de usuarios.
 *
 * Proporciona operaciones CRUD completas sobre la entidad {@link Usuario}.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final IaModeloRepository iaModeloRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lista todos los usuarios existentes.
     *
     * @return lista de usuarios como DTOs de respuesta
     */
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca un usuario por su ID.
     * 
     * @param id identificador del usuario
     * @return el usuario como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe un usuario con ese ID
     */
    public UsuarioResponseDTO buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Crea un usuario nuevo.
     * 
     * @param dto datos del usuario a crear
     * @return el usuario creado como DTO de respuesta
     * @throws RecursoDuplicadoException    si el email ya está en uso
     * @throws RecursoNoEncontradoException si el modelo de IA seleccionado no
     *                                      existe
     */
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto) {
        // 1. Verificar si el email ya está en uso
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RecursoDuplicadoException("El email " + dto.getEmail() + " ya está registrado.");
        }

        // 2. Buscar el modelo de IA base que ha elegido
        IaModelo iaModelo = iaModeloRepository.findById(dto.getIaModeloSeleccionadoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("El modelo de IA seleccionado no existe."));

        // 3. Mapear DTO a Entidad
        Usuario usuario = usuarioMapper.toEntity(dto);

        // 4. Lógica de Seguridad y Gamificación
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // Encriptación obligatoria para JWT
        usuario.setIaModeloSeleccionado(iaModelo);
        usuario.setRol(Rol.USER); // Por defecto, es un usuario normal
        usuario.setPuntos(0);
        usuario.setNivel(1);
        usuario.setFechaInscripcion(ZonedDateTime.now(ZoneId.of("Europe/Madrid")));

        // 5. Guardar en Base de Datos
        usuario = usuarioRepository.save(usuario);

        // 6. Devolver DTO limpio (sin contraseña)
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id  identificador del usuario a actualizar
     * @param dto nuevos datos del usuario
     * @return el usuario actualizado como DTO de respuesta
     * @throws RecursoNoEncontradoException si no existe un usuario con ese ID
     * @throws RecursoDuplicadoException    si el nuevo email ya está en uso por
     *                                      otro usuario
     */
    public UsuarioResponseDTO actualizar(UUID id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        // Verificar si el nuevo email ya está en uso por otro usuario
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RecursoDuplicadoException("El email " + dto.getEmail() + " ya está registrado.");
        }

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());

        // Si el password no es nulo o vacío, lo actualizamos
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        usuario.setFotoUrl(dto.getFotoUrl());

        if (dto.getIaModeloSeleccionadoId() != null) {
            IaModelo iaModelo = iaModeloRepository.findById(dto.getIaModeloSeleccionadoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El modelo de IA seleccionado no existe."));
            usuario.setIaModeloSeleccionado(iaModelo);
        }

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario a eliminar
     * @throws RecursoNoEncontradoException si no existe un usuario con ese ID
     */
    public void eliminar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        usuarioRepository.delete(usuario);
    }

    /**
     * Busca la entidad completa por email.
     * 
     * @param email correo electrónico del usuario
     * @return entidad Usuario
     * @throws RecursoNoEncontradoException si no existe un usuario con ese email
     */
    public Usuario buscarEntidadPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
    }

    /**
     * Busca la entidad completa por id.
     * 
     * @param id identificador del usuario
     * @return entidad Usuario
     * @throws RecursoNoEncontradoException si no existe un usuario con ese ID
     */
    public Usuario buscarEntidadPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }
}