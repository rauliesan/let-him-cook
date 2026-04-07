package com.daw.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para crear un comentario en un post del foro.
 *
 * @author IES Almudeyne - Let Him Cook
 */
@Data
public class PostComentarioRequestDTO {

    @NotBlank(message = "El comentario no puede estar vacío")
    private String contenido;
}
