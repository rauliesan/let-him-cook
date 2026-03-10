package com.daw.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.daw.errors.MiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handler personalizado para manejar errores de acceso denegado (HTTP 403).
 *
 * Se invoca cuando un usuario autenticado intenta acceder a un recurso
 * para el cual no tiene el rol/permiso necesario.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        MiError mierror = new MiError(HttpStatus.FORBIDDEN, LocalDateTime.now(),
                accessDeniedException.getMessage(), request.getRequestURI());
        String body = mapper.writeValueAsString(mierror);

        response.getWriter().write(body);
        response.flushBuffer();
    }
}
