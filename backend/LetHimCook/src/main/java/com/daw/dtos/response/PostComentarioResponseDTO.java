package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * DTO de respuesta para un comentario de post del foro.
 *
 * @author IES Almudeyne - Let Him Cook
 */
@Data
public class PostComentarioResponseDTO {

    private UUID id;
    private String contenido;
    private ZonedDateTime fechaCreacion;

    private UUID usuarioId;
    private String usuarioNombre;
    private String usuarioFotoUrl;

    private UUID postId;
}
