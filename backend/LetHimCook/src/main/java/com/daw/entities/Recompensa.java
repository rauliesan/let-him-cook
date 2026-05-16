package com.daw.entities;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Clase para gestionar las recompensas que se pueden conseguir en la ruleta.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "recompensa")
@Data
public class Recompensa {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String nombre;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@Column(nullable = true)
	private String emoji;

	@Column(nullable = true)
	private String rareza;

	/**
     * Probabilidad de que este premio salga en la ruleta (0.0 a 1.0)
     */
	private Double probabilidad;

	@OneToMany(mappedBy = "recompensa", cascade = CascadeType.REMOVE)
	private Set<UsuarioRecompensa> usuariosRecompensa;
}
