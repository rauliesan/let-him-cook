package com.daw.entities;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Clase para gestionar los comentarios de cada usuario a cada receta.
 * 
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "comentario")
@Data
public class Comentario {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String comentario;

	/**
	 * La valoración será de 1 a 5
	 */
	@Column(name = "valoracion")
	private Integer valoracion;

	@Column(name = "fecha_creacion", nullable = false)
	private ZonedDateTime fechaCreacion;
	
	/**
	 * Usuario al que pertenece el comentario.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	/**
	 * Receta a la que va dirigido el comentario.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receta_id", nullable = false)
	private Receta receta;

}
