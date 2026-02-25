package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad FavoritoReceta.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class FavoritoRecetaResponseDTO {
	
    private UUID id;
    
    private ZonedDateTime fechaAgregada;
    
    private UUID recetaId;
    
    private String recetaNombre;
    
}