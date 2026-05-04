package com.daw.dtos.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * DTO de solicitud para generar sugerencias de recetas con IA.
 */
@Data
public class GenerarRecetasRequestDTO {

    @NotEmpty(message = "Indica al menos un ingrediente")
    private List<String> ingredientes;

    /** Preferencias opcionales: estilo de cocina, restricciones dietéticas, etc. */
    private String preferencias;
}
