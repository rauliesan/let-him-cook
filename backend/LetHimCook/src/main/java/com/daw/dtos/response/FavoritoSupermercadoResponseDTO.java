package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad FavoritoSupermercado.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class FavoritoSupermercadoResponseDTO {
	
    private UUID id;
    
    private ZonedDateTime fechaAgregada;
    
    private UUID supermercadoId;
    
    private String supermercadoNombre;
    
}