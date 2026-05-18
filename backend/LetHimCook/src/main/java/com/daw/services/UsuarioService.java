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

/** Gestión de usuarios: CRUD, configuración IA y amistad. */
@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final IaModeloRepository iaModeloRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> listarTodos() {
    	return usuarioMapper.toListDTO(usuarioRepository.findAll());
    }

    public long contarTodos() {
        return usuarioRepository.count();
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

    /** Alias semántico de buscarPorId para el endpoint /me (permite separar lógica futura de perfil privado). */
    public UsuarioResponseDTO obtenerPerfil(UUID id) {
        return buscarPorId(id);
    }

    public UsuarioResponseDTO buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        return usuarioMapper.toResponseDTO(usuario);
    }

    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RecursoDuplicadoException("El email " + dto.getEmail() + " ya está registrado.");
        }
        if (usuarioRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RecursoDuplicadoException("El nombre de usuario " + dto.getNombre() + " ya está en uso.");
        }

        Usuario usuario = usuarioMapper.toEntity(dto);

        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(Rol.USER);
        usuario.setPuntos(0);
        usuario.setNivel(1);
        usuario.setFechaInscripcion(ZonedDateTime.now(ZoneId.of("Europe/Madrid")));

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuario);
    }

    public UsuarioResponseDTO actualizar(UUID id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RecursoDuplicadoException("El email " + dto.getEmail() + " ya está registrado.");
        }
        if (!usuario.getNombre().equalsIgnoreCase(dto.getNombre())
                && usuarioRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RecursoDuplicadoException("El nombre de usuario " + dto.getNombre() + " ya está en uso.");
        }

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        usuario.setFotoUrl(dto.getFotoUrl());

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuario);
    }

    public void eliminar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
        
        // REGLA DE NEGOCIO: No permitir borrar al último Administrador
        if (usuario.getRol().equals(Rol.ADMIN) && usuarioRepository.countByRol(Rol.ADMIN) <= 1) {
            throw new OperacionInvalidaException("No se puede eliminar al único administrador del sistema.");
        }

        usuarioRepository.delete(usuario);
    }

    /** Guarda la configuración de IA personalizada (BYOAI) del usuario. */
    public UsuarioResponseDTO guardarIaConfig(UUID usuarioId, String apiKey, String endpoint, String modelo) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        usuario.setIaCustomApiKey(apiKey);
        usuario.setIaCustomEndpoint((endpoint != null && !endpoint.isBlank()) ? endpoint : null);
        usuario.setIaCustomModelo((modelo != null && !modelo.isBlank()) ? modelo : null);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    /** Elimina la configuración de IA personalizada, vuelve al DeepSeek global. */
    public UsuarioResponseDTO eliminarIaConfig(UUID usuarioId) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        usuario.setIaCustomApiKey(null);
        usuario.setIaCustomEndpoint(null);
        usuario.setIaCustomModelo(null);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

    /** Acepta Base64 (data URI) o URL externa. */
    public UsuarioResponseDTO actualizarFoto(UUID usuarioId, String fotoUrl) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        usuario.setFotoUrl(fotoUrl);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuario));
    }

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

    public Usuario buscarEntidadPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
    }

    public Usuario buscarEntidadPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }

    public void guardarCodigoRecuperacion(Usuario usuario, String codigo) {
        usuario.setCodigoRecuperacion(codigo);
        usuario.setFechaExpiracionCodigo(ZonedDateTime.now(ZoneId.of("Europe/Madrid")).plusMinutes(15)); // expira en 15 min
        usuarioRepository.save(usuario);
    }

    /** Devuelve true si el código coincide y no ha expirado. */
    public boolean esCodigoValido(Usuario usuario, String codigo) {
        if (usuario.getCodigoRecuperacion() == null || !usuario.getCodigoRecuperacion().equals(codigo)) {
            return false;
        }
        if (usuario.getFechaExpiracionCodigo() == null || usuario.getFechaExpiracionCodigo().isBefore(ZonedDateTime.now(ZoneId.of("Europe/Madrid")))) {
            return false;
        }
        return true;
    }

    public void resetearPassword(Usuario usuario, String nuevaPassword) {
        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuario.setCodigoRecuperacion(null);
        usuario.setFechaExpiracionCodigo(null);
        usuarioRepository.save(usuario);
    }

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

    /** Deduce el coste de una tirada en slots. Lanza excepción si no tiene saldo. */
    public void cobrarTirada(UUID usuarioId, int coste) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        if (usuario.getPuntos() < coste) {
            throw new com.daw.exceptions.OperacionInvalidaException("No tienes suficientes monedas para jugar.");
        }
        usuario.setPuntos(usuario.getPuntos() - coste);
        usuarioRepository.save(usuario);
    }

    /** Heartbeat de presencia, actualiza ultimaConexion. */
    public void registrarConexion(UUID usuarioId) {
        Usuario usuario = buscarEntidadPorId(usuarioId);
        usuario.setUltimaConexion(java.time.ZonedDateTime.now(java.time.ZoneId.of("Europe/Madrid")));
        usuarioRepository.save(usuario);
    }
}