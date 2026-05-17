package com.daw.services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.UsuarioResponseDTO;
import com.daw.entities.IaModelo;
import com.daw.entities.Rol;
import com.daw.entities.Usuario;
import com.daw.exceptions.OperacionInvalidaException;
import com.daw.exceptions.RecursoDuplicadoException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.UsuarioMapper;
import com.daw.repositories.IaModeloRepository;
import com.daw.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de usuarios.
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
    	return usuarioMapper.toListDTO(usuarioRepository.findAll());
    }

    public Page<UsuarioResponseDTO> buscarPaginado(String nombre, Pageable pageable) {
        Page<Usuario> page;
        if (nombre != null && !nombre.isBlank()) {
            page = usuarioRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        } else {
            page = usuarioRepository.findAll(pageable);
        }
        return usuarioMapper.toPageDTO(page);
    }

    /**
     * Obtiene y devuelve el perfil del Usuario basado en el token actual.
     * En este caso reutilizamos la búsqueda por ID pero dejamos el método
     * semánticamente separado para futuras lógicas de Perfil Privado si las
     * hubiese.
     * 
     * @param id identificador del usuario extraído del JWT
     * @return el usuario como DTO de respuesta
     * @throws RecursoNoEncontradoException si el usuario ya no existe
     */
    public UsuarioResponseDTO obtenerPerfil(UUID id) {
        return buscarPorId(id);
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
        // 1. Verificar si el email o nombre ya están en uso
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RecursoDuplicadoException("El email " + dto.getEmail() + " ya está registrado.");
        }
        if (usuarioRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RecursoDuplicadoException("El nombre de usuario " + dto.getNombre() + " ya está en uso.");
        }

        // 2. Mapear DTO a Entidad
        Usuario usuario = usuarioMapper.toEntity(dto);

        // 4. Lógica de Seguridad y Gamificación
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // Encriptación obligatoria para JWT
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

        // Verificar si el nuevo email o nombre ya están en uso por otro usuario
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RecursoDuplicadoException("El email " + dto.getEmail() + " ya está registrado.");
        }
        if (!usuario.getNombre().equalsIgnoreCase(dto.getNombre())
                && usuarioRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RecursoDuplicadoException("El nombre de usuario " + dto.getNombre() + " ya está en uso.");
        }

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());

        // Si el password no es nulo o vacío, lo actualizamos
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        usuario.setFotoUrl(dto.getFotoUrl());

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
        
        // REGLA DE NEGOCIO: No permitir borrar al último Administrador
        if (usuario.getRol().equals(Rol.ADMIN) && usuarioRepository.countByRol(Rol.ADMIN) <= 1) {
            throw new OperacionInvalidaException("No se puede eliminar al único administrador del sistema.");
        }

        usuarioRepository.delete(usuario);
    }

    /**
     * Guarda la configuración de IA personalizada (BYOAI) del usuario.
     * La API key se almacena cifrada en la base de datos.
     */
    public UsuarioResponseDTO guardarIaConfig(UUID usuarioId, String apiKey, String endpoint, String modelo) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        usuario.setIaCustomApiKey(apiKey);
        usuario.setIaCustomEndpoint((endpoint != null && !endpoint.isBlank()) ? endpoint : null);
        usuario.setIaCustomModelo((modelo != null && !modelo.isBlank()) ? modelo : null);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    /**
     * Elimina la configuración de IA personalizada del usuario (vuelve al default).
     */
    public UsuarioResponseDTO eliminarIaConfig(UUID usuarioId) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        usuario.setIaCustomApiKey(null);
        usuario.setIaCustomEndpoint(null);
        usuario.setIaCustomModelo(null);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    /**
     * Actualiza únicamente la foto de perfil del usuario autenticado.
     * Acepta una imagen en Base64 (data URI) o una URL externa.
     *
     * @param usuarioId ID del usuario autenticado
     * @param fotoUrl   imagen en Base64 o URL
     * @return el usuario actualizado como DTO
     */
    public UsuarioResponseDTO actualizarFoto(UUID usuarioId, String fotoUrl) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        usuario.setFotoUrl(fotoUrl);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    /**
     * Actualiza el modelo de IA seleccionado por el usuario.
     */
    public UsuarioResponseDTO actualizarIaModelo(UUID usuarioId, UUID iaModeloId) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        if (iaModeloId != null) {
            IaModelo modelo = iaModeloRepository.findById(iaModeloId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Modelo de IA no encontrado con ID: " + iaModeloId));
            usuario.setIaModeloSeleccionado(modelo);
        } else {
            usuario.setIaModeloSeleccionado(null);
        }
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
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

    /**
     * Guarda el código de recuperación para un usuario.
     */
    public void guardarCodigoRecuperacion(Usuario usuario, String codigo) {
        usuario.setCodigoRecuperacion(codigo);
        // El código expira en 15 minutos
        usuario.setFechaExpiracionCodigo(ZonedDateTime.now(ZoneId.of("Europe/Madrid")).plusMinutes(15));
        usuarioRepository.save(usuario);
    }

    /**
     * Verifica si el código es válido para un usuario dado.
     */
    public boolean esCodigoValido(Usuario usuario, String codigo) {
        if (usuario.getCodigoRecuperacion() == null || !usuario.getCodigoRecuperacion().equals(codigo)) {
            return false;
        }
        if (usuario.getFechaExpiracionCodigo() == null || usuario.getFechaExpiracionCodigo().isBefore(ZonedDateTime.now(ZoneId.of("Europe/Madrid")))) {
            return false;
        }
        return true;
    }

    /**
     * Actualiza la contraseña y limpia el código de recuperación.
     */
    public void resetearPassword(Usuario usuario, String nuevaPassword) {
        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuario.setCodigoRecuperacion(null);
        usuario.setFechaExpiracionCodigo(null);
        usuarioRepository.save(usuario);
    }

    /* ── Amistad ── */

    @Transactional
    public void agregarAmigo(UUID usuarioId, UUID amigoId) {
        if (usuarioId.equals(amigoId))
            throw new OperacionInvalidaException("No puedes añadirte a ti mismo como amigo.");
        Usuario usuario = buscarEntidadPorId(usuarioId);
        Usuario amigo   = buscarEntidadPorId(amigoId);
        if (usuario.getAmigos() == null) usuario.setAmigos(new HashSet<>());
        usuario.getAmigos().add(amigo);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarAmigo(UUID usuarioId, UUID amigoId) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        if (usuario.getAmigos() != null)
            usuario.getAmigos().removeIf(a -> a.getId().equals(amigoId));
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public boolean esAmigo(UUID usuarioId, UUID amigoId) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        if (usuario.getAmigos() == null) return false;
        return usuario.getAmigos().stream().anyMatch(a -> a.getId().equals(amigoId));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> getMisAmigos(UUID usuarioId) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        if (usuario.getAmigos() == null) return List.of();
        return usuario.getAmigos().stream()
                .map(usuarioMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deduce puntos al usuario por realizar una tirada en la ruleta/slots.
     */
    public void cobrarTirada(UUID usuarioId, int coste) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        if (usuario.getPuntos() < coste) {
            throw new com.daw.exceptions.OperacionInvalidaException("No tienes suficientes monedas para jugar.");
        }
        usuario.setPuntos(usuario.getPuntos() - coste);
        usuarioRepository.save(usuario);
    }
}