package com.daw.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class PostComentarioRequestDTO {

    @NotBlank(message = "El comentario no puede estar vacío")
    private String contenido;

    private UUID recetaVinculadaId;
}
