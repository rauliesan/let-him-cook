package com.daw.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daw.dtos.request.FotoUrlRequestDTO;
import com.daw.dtos.request.UsuarioIaConfigRequestDTO;
import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.UsuarioResponseDTO;
import com.daw.security.CustomUserDetails;
import com.daw.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión de usuarios.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Lista todos los usuarios existentes.
     *
     * @return 200 OK con la lista de usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok().body(usuarioService.listarTodos());
    }

    /**
     * Búsqueda paginada de usuarios (opcionalmente filtrado por nombre).
     */
    @GetMapping("/busqueda")
    public ResponseEntity<Page<UsuarioResponseDTO>> buscarUsuarios(
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(usuarioService.buscarPaginado(nombre, pageable));
    }

    /**
     * Obtiene el perfil del usuario actualmente autenticado (vía JWT).
     *
     * @param userDetails detalles del usuario inyectados por Spring Security
     * @return 200 OK con el perfil del usuario
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obtenerMiPerfil(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok().body(usuarioService.obtenerPerfil(userDetails.getUsuario().getId()));
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return 200 OK con el usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok().body(usuarioService.buscarPorId(id));
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param dto datos del usuario a crear
     * @return 201 Created con el usuario creado
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.registrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un usuario existente. En el PreAuthorize se comprueba si el usuario es ADMIN o si
     * autenticado es el mismo que el que se quiere actualizar.
     *
     * @param id  identificador del usuario a actualizar
     * @param dto nuevos datos del usuario
     * @return 200 OK con el usuario actualizado
     */
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.usuario.id")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable UUID id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok().body(usuarioService.actualizar(id, dto));
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario a eliminar
     * @return 204 No Content
     */
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.usuario.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza el modelo de IA preferido del usuario autenticado.
     * Pasa iaModeloId=null para desvincularlo.
     */
    @PutMapping("/me/ia-modelo")
    public ResponseEntity<UsuarioResponseDTO> actualizarIaModelo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) UUID iaModeloId) {
        return ResponseEntity.ok(usuarioService.actualizarIaModelo(userDetails.getUsuario().getId(), iaModeloId));
    }

    /**
     * Guarda la configuración de IA personalizada (BYOAI) en el perfil del usuario.
     * La API key nunca se devuelve en la respuesta.
     */
    @PutMapping("/me/ia-config")
    public ResponseEntity<UsuarioResponseDTO> guardarIaConfig(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UsuarioIaConfigRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.guardarIaConfig(
                userDetails.getUsuario().getId(),
                dto.getApiKey(),
                dto.getEndpoint(),
                dto.getModelo()));
    }

    /**
     * Elimina la configuración de IA personalizada — vuelve a usar la IA por defecto.
     */
    @DeleteMapping("/me/ia-config")
    public ResponseEntity<UsuarioResponseDTO> eliminarIaConfig(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(usuarioService.eliminarIaConfig(userDetails.getUsuario().getId()));
    }

    /**
     * Actualiza únicamente la foto de perfil del usuario autenticado.
     * Acepta imagen en Base64 (data URI) o URL externa.
     */
    @PatchMapping("/me/foto")
    public ResponseEntity<UsuarioResponseDTO> actualizarFoto(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FotoUrlRequestDTO dto) {
        return ResponseEntity.ok(
                usuarioService.actualizarFoto(userDetails.getUsuario().getId(), dto.getFotoUrl()));
    }

    /* ── Amistad ── */

    @PostMapping("/{id}/amigos")
    public ResponseEntity<Void> agregarAmigo(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        usuarioService.agregarAmigo(userDetails.getUsuario().getId(), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/amigos")
    public ResponseEntity<Void> eliminarAmigo(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        usuarioService.eliminarAmigo(userDetails.getUsuario().getId(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/amigos")
    public ResponseEntity<List<UsuarioResponseDTO>> getMisAmigos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(usuarioService.getMisAmigos(userDetails.getUsuario().getId()));
    }

    @GetMapping("/{id}/es-amigo")
    public ResponseEntity<Map<String, Boolean>> esAmigo(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean esAmigo = usuarioService.esAmigo(userDetails.getUsuario().getId(), id);
        return ResponseEntity.ok(Map.of("esAmigo", esAmigo));
    }

    /**
     * Deduce puntos al usuario por una tirada en la ruleta/slots.
     */
    @PostMapping("/me/cobrar-tirada")
    public ResponseEntity<Void> cobrarTirada(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "100") int coste) {
        usuarioService.cobrarTirada(userDetails.getUsuario().getId(), coste);
        return ResponseEntity.ok().build();
    }
}
