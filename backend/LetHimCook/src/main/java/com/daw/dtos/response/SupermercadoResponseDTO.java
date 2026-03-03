package com.daw.dtos.response;

import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad Supermercado.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class SupermercadoResponseDTO {
	
    private UUID id;
    
    private String nombre;
    
    private String descripcion;
    
    private Double valoracion;
    
    private String direccion;
    
    private String horario;
    
    private String fotoUrl;
    
}