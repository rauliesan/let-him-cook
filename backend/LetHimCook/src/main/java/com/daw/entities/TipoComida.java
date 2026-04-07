package com.daw.entities;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Clase para gestionar los tipos de comida que existen y poder etiquetar recetas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "tipo_comida")
@Data
public class TipoComida {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false, unique = true)
	private String nombre;
	
	@Column(columnDefinition = "TEXT")
	private String descripcion;
	
	/* Emoji de la categoría (antes se almacenaba una ruta de imagen) */
	@Column(name = "icono_url", columnDefinition = "TEXT")
	private String iconoUrl;

	/* Color base de la categoría en formato hex (#RRGGBB) */
	@Column(name = "color_hex", length = 7)
	private String colorHex;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoComida")
	private Set<Receta> recetas;
	
}
