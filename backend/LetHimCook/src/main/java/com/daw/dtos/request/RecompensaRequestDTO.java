package com.daw.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RequestDTO para que un administrador pueda crear o editar Recompensas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class RecompensaRequestDTO {
	
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    private String emoji;

    private String rareza;

    @Min(value = 0) @Max(value = 1)
    private Double probabilidad;
}