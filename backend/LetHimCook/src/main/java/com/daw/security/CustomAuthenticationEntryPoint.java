package com.daw.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.daw.errors.MiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Punto de entrada personalizado para manejar errores de autenticación (HTTP
 * 401).
 *
 * Se invoca cuando un usuario no autenticado intenta acceder a un recurso
 * protegido.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        MiError mierror = new MiError(HttpStatus.UNAUTHORIZED, LocalDateTime.now(),
                authException.getMessage(), request.getRequestURI());
        String body = mapper.writeValueAsString(mierror);

        response.getWriter().write(body);
        response.flushBuffer();
    }
}
