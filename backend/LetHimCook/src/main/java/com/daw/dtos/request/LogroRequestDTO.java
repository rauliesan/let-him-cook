package com.daw.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RequestDTO para que el administrador pueda crear o editar Logros.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class LogroRequestDTO {
	
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    private String icono;
}

