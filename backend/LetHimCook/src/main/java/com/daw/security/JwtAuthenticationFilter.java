package com.daw.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro que intercepta cada petición HTTP para validar el token JWT.
 *
 * Extrae la cabecera Authorization: Bearer.
 * Obtiene el username (email) del token mediante JwtService.
 * Carga el usuario desde {@link CustomUserDetailsService}.
 * Si el token es válido, establece la autenticación en el SecurityContextHolder.
 * </ol>
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener la cabecera Authorization
        final String authHeader = request.getHeader("Authorization");

        // Si no hay cabecera o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (quitar "Bearer ")
        final String jwt = authHeader.substring(7);

        // 3. Extraer el username del token
        final String username = jwtService.extractUsername(jwt);

        // 4. Si tenemos un username y no hay autenticación previa en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Cargar los datos del usuario desde la BD
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 6. Validar el token contra los datos del usuario
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Crear el objeto de autenticación y establecerlo en el contexto
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Continuar la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
