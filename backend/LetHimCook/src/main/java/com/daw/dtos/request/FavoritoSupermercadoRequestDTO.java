package com.daw.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * RequestDTO para que el usuario pueda seleccionar su Supermercado Favorito.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class FavoritoSupermercadoRequestDTO {
	
    @NotNull(message = "El supermercado id es obligatorio")
    private UUID supermercadoId;
    
}