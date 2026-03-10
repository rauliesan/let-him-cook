package com.daw.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.daw.dtos.request.LoginRequestDTO;
import com.daw.dtos.request.UsuarioRequestDTO;
import com.daw.dtos.response.AuthResponseDTO;
import com.daw.entities.Usuario;
import com.daw.mappers.AuthMapper;
import com.daw.security.CustomUserDetails;
import com.daw.security.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de autenticación que gestiona el login y el registro de usuarios.
 * 
 * El login autentica las credenciales a través del AuthenticationManager
 * de Spring Security y devuelve un token JWT si las credenciales son correctas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final AuthMapper authMapper;

    /**
     * Registra un nuevo usuario y devuelve un token JWT.
     * 
     * @param dto datos del nuevo usuario
     * @return respuesta con token JWT y datos básicos del usuario
     */
    public AuthResponseDTO register(UsuarioRequestDTO dto) {
        // 1. Registrar el usuario (se delega toda la lógica a UsuarioService)
        usuarioService.registrarUsuario(dto);

        // 2. Cargar la entidad completa para generar el token
        Usuario usuario = usuarioService.buscarEntidadPorEmail(dto.getEmail());
        CustomUserDetails userDetails = new CustomUserDetails(usuario);

        // 3. Generar token JWT
        String token = jwtService.generateToken(userDetails);

        // 4. Mapear respuesta
        return authMapper.toAuthResponse(usuario, token);
    }

    /**
     * Autentica un usuario existente por email y contraseña.
     * 
     * @param dto email y contraseña del usuario
     * @return respuesta con token JWT y datos básicos del usuario
     * @throws BadCredentialsException si las credenciales son incorrectas
     */
    public AuthResponseDTO login(LoginRequestDTO dto) {
        // 1. Autenticar credenciales (Spring Security lanza excepción si fallan)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        // 2. Obtener los detalles del usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        // 3. Generar token JWT
        String token = jwtService.generateToken(userDetails);

        // 4. Mapear respuesta
        return authMapper.toAuthResponse(usuario, token);
    }
}
