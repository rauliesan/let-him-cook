package com.daw.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.daw.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Carga usuarios desde la BD para Spring Security.
 * Se invoca durante el login y en el filtro JWT para reconstruir el contexto de seguridad.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró un usuario con el email: " + email));
    }
}
