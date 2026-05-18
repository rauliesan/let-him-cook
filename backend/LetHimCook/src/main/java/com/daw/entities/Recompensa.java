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

	private Double probabilidad; // 0.0 a 1.0

	@OneToMany(mappedBy = "recompensa", cascade = CascadeType.REMOVE)
	private Set<UsuarioRecompensa> usuariosRecompensa;
}
