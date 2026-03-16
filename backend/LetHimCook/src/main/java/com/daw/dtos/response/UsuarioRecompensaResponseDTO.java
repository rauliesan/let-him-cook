package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class UsuarioRecompensaResponseDTO {
    private UUID id;
    private RecompensaResponseDTO recompensa;
    private ZonedDateTime fechaObtenida;
}
