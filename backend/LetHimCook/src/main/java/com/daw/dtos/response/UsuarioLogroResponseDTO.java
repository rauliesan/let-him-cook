package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad UsuarioLogro.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class UsuarioLogroResponseDTO {
	
    private UUID id;
    
    private ZonedDateTime fechaObtenido;
    
    private UUID logroId;
    
    private String logroNombre;
    
    private String logroIcono;
    
}