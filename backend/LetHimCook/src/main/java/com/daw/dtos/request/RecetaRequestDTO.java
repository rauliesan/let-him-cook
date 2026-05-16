package com.daw.dtos.request;

import java.util.UUID;

import com.daw.entities.Dificultad;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * RequestDTO para que el usuario pueda crear o editar sus Recetas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class RecetaRequestDTO {
	
    @NotBlank(message = "El nombre de la receta es obligatorio")
    private String nombre;
    
    private String descripcion;

    @NotBlank(message = "Los ingredientes son obligatorios")
    private String ingredientes;

    private String instrucciones;

    @Min(value = 1, message = "El tiempo de preparación debe ser mayor a 0")
    private Integer tiempoPreparacion;

    private Dificultad dificultad;

    @Min(value = 0, message = "Las calorías no pueden ser negativas")
    private Integer calorias;

    private String alergenos;
    
    private Boolean esPublica = true;
    
    private String imagenUrl;

    private UUID tipoComidaId;
    
    private UUID iaModeloId;
}