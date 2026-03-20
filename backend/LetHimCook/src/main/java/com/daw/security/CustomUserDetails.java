package com.daw.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.daw.entities.Usuario;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador que envuelve la entidad Usuario para que sea compatible
 * con el contrato UserDetails de Spring Security.
 *
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    /**
     * Devuelve la autoridad del usuario basándose en su rol.
     * Spring Security requiere el prefijo ROLE_ para usar
     * hasRole().
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    /**
     * Se utiliza el email como identificador de autenticación.
     */
    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Proporciona acceso a la entidad Usuario subyacente,
     * útil cuando se necesitan campos adicionales fuera del contrato de
     * UserDetails.
     *
     * @return la entidad Usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }
}
