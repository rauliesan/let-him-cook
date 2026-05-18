package com.daw.dtos.request;

import lombok.Data;

@Data
public class FotoUrlRequestDTO {

    /** Imagen en Base64 (data:image/png;base64,...) o URL externa */
    private String fotoUrl;
}
