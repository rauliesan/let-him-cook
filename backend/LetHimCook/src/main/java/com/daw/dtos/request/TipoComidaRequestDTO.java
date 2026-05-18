package com.daw.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RequestDTO para que un administrador pueda crear o editar los Tipos de Comida.
 *
 */
@Data
public class TipoComidaRequestDTO {
	
    @NotBlank(message = "El nombre del tipo de comida es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    private String iconoUrl;
}