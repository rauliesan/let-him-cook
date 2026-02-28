package com.daw.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Clase para gestionar los supermercados.
 * Son añadidos por un administrador.
 * Los usuarios pueden seleccionar sus supermercados favoritos y después filtrarlos en una implementación de Google Maps en el frontend.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "supermercado")
@Data
public class Supermercado {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String nombre;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	private Double valoracion;

	private String direccion;
	
	private String horario;

	@Column(name = "foto_url", columnDefinition = "TEXT")
	private String fotoUrl;

}
