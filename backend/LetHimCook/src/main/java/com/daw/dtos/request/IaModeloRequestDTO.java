package com.daw.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * RequestDTO para que el administrador pueda crear o editar modelos de IA que el usuario podrá utilizar.
 *
 */
@Data
public class IaModeloRequestDTO {
	
    @NotBlank(message = "El nombre del modelo es obligatorio")
    private String nombreModelo;
    
    @NotNull(message = "La API padre es obligatoria")
    private UUID apiId;
}