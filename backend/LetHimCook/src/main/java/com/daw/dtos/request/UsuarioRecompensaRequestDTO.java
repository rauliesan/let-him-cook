package com.daw.dtos.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioRecompensaRequestDTO {
    @NotNull(message = "El ID de la recompensa es obligatorio")
    private UUID recompensaId;
}
