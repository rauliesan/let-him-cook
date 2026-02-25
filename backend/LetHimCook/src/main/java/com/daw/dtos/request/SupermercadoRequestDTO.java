package com.daw.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RequestDTO para que un administrador pueda crear o editar los Supermercados.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class SupermercadoRequestDTO {
	
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @Min(value = 0) @Max(value = 5)
    private Double valoracion;
    
    private String direccion;
    
    private String horario;
    
    private String fotoUrl;
}