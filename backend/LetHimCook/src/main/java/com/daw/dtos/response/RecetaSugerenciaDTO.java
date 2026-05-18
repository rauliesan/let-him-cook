package com.daw.dtos.response;

import lombok.Data;

/**
 * DTO que representa una sugerencia de receta generada por IA.
 */
@Data
public class RecetaSugerenciaDTO {

    private String nombre;
    private String descripcion;
    private String ingredientes;
    private String instrucciones;
    private Integer tiempoPreparacion;
    private String dificultad;
    private Integer calorias;
    private String alergenos;
    private String categoria;
    private String categoriaEmoji;
    private String categoriaColor;
}
