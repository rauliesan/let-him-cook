package com.daw.entities;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
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
 * Clase para gestionar las APIs disponibles para los usuarios.
 * 
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "api")
@Data
public class Api {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(name = "nombre_servicio", nullable = false)
	private String nombreServicio;
	
	@Column(name = "endpoint_url")
	private String endpointUrl;
	
	@Column(name = "api_key", columnDefinition = "TEXT")
	private String apiKey;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "api", cascade = CascadeType.REMOVE)
	private Set<IaModelo> modelos;
	
}
