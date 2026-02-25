package com.daw.dtos.response;

import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad TipoComida.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class TipoComidaResponseDTO {
	
    private UUID id;
    
    private String nombre;
    
    private String descripcion;
    
    private String icono;
    
}