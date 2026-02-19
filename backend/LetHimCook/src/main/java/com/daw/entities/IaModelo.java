package com.daw.entities;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Clase para gestionar los modelos de ia que podrá usar el usuario.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "ia_modelo")
@Data
public class IaModelo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(name = "nombre_modelo", nullable = false)
	private String nombreModelo;
	
	/**
	 * Api del modelo de ia, que contiene todo lo necesario para poder hacer la conexión.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "api_id", nullable = false)
	private Api api;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "iaModeloSeleccionado")
	private Set<Usuario> usuarios;
	
}
