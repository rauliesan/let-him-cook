package com.daw.entities;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Formula;

/**
 * Clase para gestionar las recetas creadas.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "receta")
@Data
public class Receta {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String nombre;
	
	@Column(columnDefinition = "TEXT")
	private String descripcion;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String ingredientes;
	
	@Column(columnDefinition = "TEXT")
	private String instrucciones;
	
	/**
	 * Tiempo de preparación en minutos.
	 */
	@Column(name = "tiempo_preparacion")
	private Integer tiempoPreparacion;
	
	@Enumerated(EnumType.STRING)
	@Column(name= "dificultad")
	private Dificultad dificultad;
	
	private Integer calorias;
	
	@Column(columnDefinition = "TEXT")
	private String alergenos;
	
	@Column(name = "es_publica")
	private Boolean esPublica;
	
	@Column(name = "imagen_url", columnDefinition = "TEXT")
	private String imagenUrl;
	
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private ZonedDateTime fechaCreacion;
	
	/* Categoría principal — determina el color de la tarjeta */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_comida_id")
	private TipoComida tipoComida;

	/* Hasta dos categorías adicionales opcionales */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_comida2_id")
	private TipoComida tipoComida2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_comida3_id")
	private TipoComida tipoComida3;
	
	/**
	 * Usuario creador de la receta.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_creador_id", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ia_modelo_id")
	private IaModelo iaModelo;

	@OneToMany(mappedBy = "receta", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Set<Comentario> comentarios;

	@OneToMany(mappedBy = "receta", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Set<FavoritoReceta> favoritosRecetas;
	
	
	/* Número de usuarios que han marcado esta receta como favorita.
	   Se calcula mediante subconsulta SQL — no es una columna real en la BD. */
	@Formula("(SELECT COUNT(*) FROM favorito_receta fr WHERE fr.receta_id = id)")
	private long totalLikes;

	@PrePersist
	protected void onCreate() {
	    // Solo asigna la fecha automática si el campo está vacío (es null)
	    if (this.fechaCreacion == null) {
	        this.fechaCreacion = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
	    }
	}
	
}
