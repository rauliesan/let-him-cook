package com.daw.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.daw.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de UserDetailsService que carga usuarios desde la base
 * de datos
 * a través de UsuarioRepository.
 *
 * Spring Security invoca #loadUserByUsername(String) durante la
 * autenticación
 * y en el filtro JWT para reconstruir el contexto de seguridad.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Busca un usuario por email y lo envuelve en {@link CustomUserDetails}.
     *
     * @param email email del usuario (usado como username)
     * @return UserDetails del usuario encontrado
     * @throws UsernameNotFoundException si no existe un usuario con ese email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se encontró un usuario con el email: " + email));
    }
}
