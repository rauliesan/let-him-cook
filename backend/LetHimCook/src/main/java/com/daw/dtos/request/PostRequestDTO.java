package com.daw.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para crear un post en el foro.
 *
 * @author IES Almudeyne - Let Him Cook
 */
@Data
public class PostRequestDTO {

    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    @NotBlank(message = "El contenido no puede estar vacío")
    private String contenido;

    /* Imagen opcional */
    private String imagenUrl;

    /* Receta vinculada opcional */
    private UUID recetaVinculadaId;
}
