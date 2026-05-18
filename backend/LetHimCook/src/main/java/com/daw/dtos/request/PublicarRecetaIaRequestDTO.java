package com.daw.dtos.request;

import java.util.UUID;

import com.daw.entities.Dificultad;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PublicarRecetaIaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String descripcion;

    @NotBlank(message = "Los ingredientes son obligatorios")
    private String ingredientes;

    private String instrucciones;

    private Integer tiempoPreparacion;

    private Dificultad dificultad;

    private Integer calorias;

    private String alergenos;

    private Boolean esPublica = true;

    /** Nombre de la categoría, se busca en BD o se crea si no existe */
    private String categoriaNombre;
    private String categoriaEmoji;
    private String categoriaColor;

    private UUID iaModeloId;
}
