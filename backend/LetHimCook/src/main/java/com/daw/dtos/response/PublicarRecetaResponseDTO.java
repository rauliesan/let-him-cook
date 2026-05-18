package com.daw.dtos.response;

import lombok.Data;

/**
 * DTO de respuesta al publicar una receta generada por IA.
 * Incluye la receta creada y los puntos ganados.
 */
@Data
public class PublicarRecetaResponseDTO {

    private RecetaResponseDTO receta;
    private int puntosGanados;
    private int nuevosTotalPuntos;
}
