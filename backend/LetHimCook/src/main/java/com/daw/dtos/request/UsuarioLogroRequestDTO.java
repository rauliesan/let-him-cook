package com.daw.dtos.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioLogroRequestDTO {
    @NotNull(message = "El ID del logro es obligatorio")
    private UUID logroId;
}
