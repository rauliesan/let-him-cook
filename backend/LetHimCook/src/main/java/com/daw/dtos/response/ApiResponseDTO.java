package com.daw.dtos.response;

import java.util.UUID;

import lombok.Data;

/**
 * ResponseDTO para la entidad API.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class ApiResponseDTO {
	
    private UUID id;
    
    private String nombreServicio;
    
    private String endpointUrl;
    
    private String apiKey;
}

