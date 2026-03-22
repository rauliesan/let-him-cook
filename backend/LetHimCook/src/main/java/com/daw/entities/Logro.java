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
 * Clase para la gestión de logros que puede obtener el usuario.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "logro")
@Data
public class Logro {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(name = "nombre", nullable = false, unique = true)
	private String nombre;
	
	@Column(columnDefinition = "TEXT")
	private String descripcion;
	
	@Column(name = "icono_url", columnDefinition = "TEXT")
	private String iconoUrl;

	@OneToMany(mappedBy = "logro", cascade = CascadeType.REMOVE)
	private Set<UsuarioLogro> usuariosLogro;
	
}
