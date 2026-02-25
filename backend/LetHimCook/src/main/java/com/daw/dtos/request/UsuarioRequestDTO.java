package com.daw.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * RequestDTO para que el usuario pueda crear o editar su perfil.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Data
public class UsuarioRequestDTO {
	
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String fotoUrl;
    
    @NotNull(message = "Debe seleccionar un modelo de IA base")
    private UUID iaModeloSeleccionadoId;
}