package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class PostComentarioResponseDTO {

    private UUID id;
    private String contenido;
    private ZonedDateTime fechaCreacion;

    private UUID usuarioId;
    private String usuarioNombre;
    private String usuarioFotoUrl;

    private UUID postId;

    private UUID recetaVinculadaId;
    private String recetaVinculadaNombre;
}
