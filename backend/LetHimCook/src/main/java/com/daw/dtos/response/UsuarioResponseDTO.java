package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad Usuario.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class UsuarioResponseDTO {
	
    private UUID id;
    
    private String nombre;
    
    private String email;
    
    private Integer puntos;
    
    private Integer nivel;
    
    private ZonedDateTime fechaInscripcion;
    
    private String fotoUrl;
    
    // Relación aplanada
    private UUID iaModeloSeleccionadoId;
    private String iaModeloSeleccionadoNombre;
    
}