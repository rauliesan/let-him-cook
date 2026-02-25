package com.daw.dtos.response;

import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad Recompensa.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class RecompensaResponseDTO {
	
    private UUID id;
    
    private String nombre;
    
    private String descripcion;
    
    private Double probabilidad;
    
}