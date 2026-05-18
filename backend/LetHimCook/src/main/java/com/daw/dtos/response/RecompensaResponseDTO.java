package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad Recompensa.
 *
 */
@Data
public class RecompensaResponseDTO {
	
	private UUID id;
	
    private String nombre;
    
    private String descripcion;
    
    private String emoji;

    private String rareza;

    private Double probabilidad;
    
    private ZonedDateTime fechaObtenida;
    
}