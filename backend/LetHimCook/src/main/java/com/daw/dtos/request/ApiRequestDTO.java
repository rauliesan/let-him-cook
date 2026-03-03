package com.daw.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RequestDTO para que un administrador pueda añadir o editar una API.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class ApiRequestDTO {
	
    @NotBlank(message = "El nombre del servicio es obligatorio")
    private String nombreServicio;
    
    private String endpointUrl;
    
    @NotBlank(message = "La API Key es obligatoria")
    private String apiKey;
}

