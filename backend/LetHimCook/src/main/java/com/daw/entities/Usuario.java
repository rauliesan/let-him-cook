package com.daw.entities;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String nombre;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;
	
	@Column(nullable = false)
	private Integer puntos;
	
	@Column(nullable = false)
	private Integer nivel;
	
	@CreationTimestamp
    @Column(name = "fecha_inscripcion", nullable = false, updatable = false)
    private ZonedDateTime fechaInscripcion;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ia_modelo_seleccionado_id", nullable = false)
	private IaModelo iaModeloSeleccionado;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
	private Set<Receta> recetas;
	
	 @ManyToMany(fetch = FetchType.LAZY)
	 @JoinTable(
		 name = "usuario_preferencia",
		 joinColumns = @JoinColumn(name = "usuario_id"),
		 inverseJoinColumns = @JoinColumn(name = "tipo_comida_id")
	 )
	 private Set<TipoComida> preferencias;
	 
	 @ManyToMany
	 @JoinTable(
	     name = "usuario_amigo",
	     joinColumns = @JoinColumn(name = "usuario_id"),
	     inverseJoinColumns = @JoinColumn(name = "amigo_id")
	 )
	 private Set<Usuario> amigos;
	
}
