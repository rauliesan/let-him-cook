package com.daw.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	private Double probabilidad; // numeric en SQL (0.0 a 1.0)
}
