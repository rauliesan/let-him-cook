package com.daw.dtos.request;

import lombok.Data;

@Data
public class UsuarioIaConfigRequestDTO {

    private String apiKey;

    // por defecto usa el endpoint de DeepSeek
    private String endpoint;

    // por defecto deepseek-chat
    private String modelo;
}
