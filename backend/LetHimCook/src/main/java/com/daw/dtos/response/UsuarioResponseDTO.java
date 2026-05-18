package com.daw.dtos.response;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.daw.entities.Rol;

import lombok.Data;

/**
 * ResponseDTO para la entidad Usuario.
 *
 */
@Data
public class UsuarioResponseDTO {

    private UUID id;

    private String nombre;

    private String email;

    private Integer puntos;

    private Integer nivel;

    private ZonedDateTime fechaInscripcion;

    private String fotoUrl;

    private Rol rol;

    private UUID iaModeloSeleccionadoId;

    private String iaModeloSeleccionadoNombre;

    /** true si el usuario tiene su propia API key guardada en el perfil */
    private boolean iaCustomConfigured;

    /** Endpoint de la IA personalizada (nunca se devuelve la key) */
    private String iaCustomEndpoint;

    /** Nombre del modelo de la IA personalizada */
    private String iaCustomModelo;

    /** Última conexión del usuario (para indicador de presencia online) */
    private ZonedDateTime ultimaConexion;

}