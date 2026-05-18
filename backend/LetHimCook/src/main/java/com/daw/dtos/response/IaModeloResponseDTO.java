package com.daw.dtos.response;

import lombok.Data;
import java.util.UUID;

/**
 * ResponseDTO para la entidad IaModelo.
 *
 */
@Data
public class IaModeloResponseDTO {
	
    private UUID id;
    
    private String nombreModelo;
    
    private UUID apiId;
    
    private String apiNombreServicio;
    
    private String apiEndpointUrl;
    
    private String apiKey;
    
}