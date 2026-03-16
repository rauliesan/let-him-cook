package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class UsuarioLogroResponseDTO {
    private UUID id;
    private LogroResponseDTO logro;
    private ZonedDateTime fechaObtenido;
}
