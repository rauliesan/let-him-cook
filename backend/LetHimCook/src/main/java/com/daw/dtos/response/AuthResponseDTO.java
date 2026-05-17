package com.daw.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * DTO de respuesta para las operaciones de autenticación (login y registro).
 * Contiene el token JWT y los datos básicos del usuario autenticado.
 *
 * @author IES Almudeyne - Let Him Cook
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {

    private UUID id;
    private String token;
    private String email;
    private String nombre;
    private String rol;
}
