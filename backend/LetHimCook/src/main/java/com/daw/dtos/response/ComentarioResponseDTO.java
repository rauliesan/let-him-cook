package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad Comentario.
 *
 */
@Data
public class ComentarioResponseDTO {
	
    private UUID id;
    
    private String comentario;
    
    private Integer valoracion;
    
    private ZonedDateTime fechaCreacion;
    
    private UUID usuarioId;
    
    private String usuarioNombre;
    
    private String usuarioFotoUrl; // Útil para mostrar el avatar en la UI junto al comentario
    
    private UUID recetaId;
}