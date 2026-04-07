package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * DTO de respuesta para un post del foro.
 *
 * @author IES Almudeyne - Let Him Cook
 */
@Data
public class PostResponseDTO {

    private UUID id;
    private String titulo;
    private String contenido;
    private String imagenUrl;
    private ZonedDateTime fechaCreacion;
    private long totalComentarios;

    /* Autor */
    private UUID usuarioId;
    private String usuarioNombre;
    private String usuarioFotoUrl;

    /* Receta vinculada (opcional) */
    private UUID recetaVinculadaId;
    private String recetaVinculadaNombre;
}
