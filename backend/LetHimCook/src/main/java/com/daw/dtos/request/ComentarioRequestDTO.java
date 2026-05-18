package com.daw.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * RequestDTO para que el usuario pueda crear o editar Comentarios.
 *
 */
@Data
public class ComentarioRequestDTO {
	
    @NotBlank(message = "El comentario no puede estar vacío")
    private String comentario;

    @Min(value = 1, message = "La valoración mínima es 1") 
    @Max(value = 5, message = "La valoración máxima es 5")
    private Integer valoracion;

    @NotNull(message = "La receta a comentar es obligatoria")
    private UUID recetaId;
    
    // El usuarioId no se pide aquí, se sacará del token JWT en el controlador.
}
