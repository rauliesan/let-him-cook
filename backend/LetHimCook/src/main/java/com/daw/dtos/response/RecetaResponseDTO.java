package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.daw.entities.Dificultad;

import lombok.Data;

/**
 * ResponseDTO para la entidad Receta.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class RecetaResponseDTO {
	
    private UUID id;
    
    private String nombre;
    
    private String descripcion;
    
    private String ingredientes;
    
    private Integer tiempoPreparacion;
    
    private Dificultad dificultad;
    
    private Integer calorias;
    
    private String alergenos;
    
    private Boolean esPublica;
    
    private String imagenUrl;
    
    private ZonedDateTime fechaCreacion;

    // Relaciones aplanadas
    private UUID tipoComidaId;
    private String tipoComidaNombre;
    
    private UUID usuarioCreadorId;
    private String usuarioCreadorNombre;
}