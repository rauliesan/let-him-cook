package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad UsuarioRecompensa.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class UsuarioRecompensaResponseDTO {
	
    private UUID id;
    
    private ZonedDateTime fechaObtenida;
    
    private UUID recompensaId;
    
    private String recompensaNombre;
    
}