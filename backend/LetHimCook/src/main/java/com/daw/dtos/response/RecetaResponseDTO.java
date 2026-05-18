package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.daw.entities.Dificultad;

import lombok.Data;

/**
 * ResponseDTO para la entidad Receta.
 *
 */
@Data
public class RecetaResponseDTO {

    private UUID id;

    private String nombre;

    private String descripcion;

    private String ingredientes;

    private String instrucciones;

    private Integer tiempoPreparacion;

    private Dificultad dificultad;

    private Integer calorias;

    private String alergenos;

    private Boolean esPublica;

    private String imagenUrl;

    private ZonedDateTime fechaCreacion;

    // Relaciones aplanadas, hasta 3 categorías por receta
    private UUID tipoComidaId;
    private String tipoComidaNombre;
    private String tipoComida2Nombre;
    private String tipoComida3Nombre;

    private UUID usuarioCreadorId;
    private String usuarioCreadorNombre;

    private UUID iaModeloId;

    /* Total de usuarios que han marcado esta receta como favorita */
    private long totalLikes;
}
