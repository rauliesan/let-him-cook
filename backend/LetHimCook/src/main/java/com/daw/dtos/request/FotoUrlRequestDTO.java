package com.daw.dtos.request;

import lombok.Data;

/**
 * DTO para actualizar únicamente la foto de perfil del usuario.
 * Acepta una imagen en formato Base64 (data URI) o una URL externa.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class FotoUrlRequestDTO {

    /** Imagen en Base64 (data:image/png;base64,...) o URL externa */
    private String fotoUrl;
}
