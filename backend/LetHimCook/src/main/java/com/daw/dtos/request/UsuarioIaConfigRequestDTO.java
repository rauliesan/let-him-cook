package com.daw.dtos.request;

import lombok.Data;

/**
 * DTO para guardar la configuración de IA personalizada del usuario.
 */
@Data
public class UsuarioIaConfigRequestDTO {

    /** API key de la IA personalizada */
    private String apiKey;

    /** URL del endpoint (opcional — por defecto DeepSeek) */
    private String endpoint;

    /** Nombre del modelo (opcional — por defecto deepseek-chat) */
    private String modelo;
}
