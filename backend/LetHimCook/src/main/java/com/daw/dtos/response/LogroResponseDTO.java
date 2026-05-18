package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad Logro.
 *
 */
@Data
public class LogroResponseDTO {
	
	private UUID id;
	
    private String nombre;
    
    private String descripcion;
    
    private String iconoUrl;
    
    private ZonedDateTime fechaObtenido; // Si es null, el usuario no lo tiene. Si tiene fecha, ya lo consiguió.
}

