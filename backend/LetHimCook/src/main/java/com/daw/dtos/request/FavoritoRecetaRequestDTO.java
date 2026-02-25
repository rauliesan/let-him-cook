package com.daw.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * RequestDTO para que el usuario pueda seleccionar sus Recetas Favoritas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class FavoritoRecetaRequestDTO {
	
    @NotNull(message = "La receta id es obligatoria")
    private UUID recetaId;
}

