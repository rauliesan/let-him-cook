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

/** Login, registro y flujo de recuperación de contraseña (3 pasos: envío código → verificación → reset). */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final EmailService emailService;
    private final AuthMapper authMapper;

    public AuthResponseDTO register(UsuarioRequestDTO dto) {
        usuarioService.registrarUsuario(dto);
        Usuario usuario = usuarioService.buscarEntidadPorEmail(dto.getEmail());
        CustomUserDetails userDetails = new CustomUserDetails(usuario);
        String token = jwtService.generateToken(userDetails);
        return authMapper.toAuthResponse(usuario, token);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();
        String token = jwtService.generateToken(userDetails);
        return authMapper.toAuthResponse(usuario, token);
    }

    /** Paso 1: genera un código de 6 dígitos y lo envía por email. */
    public void solicitarRecuperacion(String email) {
        try {
            Usuario usuario = usuarioService.buscarEntidadPorEmail(email);
            String codigo = String.format("%06d", new java.util.Random().nextInt(999999));
            usuarioService.guardarCodigoRecuperacion(usuario, codigo);
            emailService.enviarCodigoRecuperacion(email, codigo);
        } catch (com.daw.exceptions.RecursoNoEncontradoException e) {
            throw new com.daw.exceptions.RecursoNoEncontradoException("El correo ingresado no está registrado en el sistema.");
        }
    }

    /** Paso 2: devuelve true si el código es correcto y no ha expirado. */
    public boolean verificarCodigo(String email, String codigo) {
        try {
            Usuario usuario = usuarioService.buscarEntidadPorEmail(email);
            return usuarioService.esCodigoValido(usuario, codigo);
        } catch (com.daw.exceptions.RecursoNoEncontradoException e) {
            return false;
        }
    }

    /** Paso 3: cambia la contraseña si el código es válido. */
    public void resetearPassword(String email, String codigo, String nuevaPassword) {
        Usuario usuario = usuarioService.buscarEntidadPorEmail(email);
        if (!usuarioService.esCodigoValido(usuario, codigo)) {
            throw new org.springframework.security.authentication.BadCredentialsException("Código inválido o expirado");
        }
        usuarioService.resetearPassword(usuario, nuevaPassword);
    }
}
