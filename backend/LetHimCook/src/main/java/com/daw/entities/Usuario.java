package com.daw.entities;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Clase para gestionar los usuarios.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "usuario")
@Data
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true)
	private String nombre;

	@Column(nullable = false, unique = true)
	private String email;

	/**
	 * Contraseña encriptada.
	 */
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	/**
	 * Puntos que se van acumulando cada vez que el usuario consiga un logro.
	 */
	@Column(nullable = false)
	private Integer puntos = 0;

	/**
	 * Nivel que va aumentando con los puntos.
	 */
	@Column(nullable = false)
	private Integer nivel = 1;

	@Column(name = "fecha_inscripcion", nullable = false, updatable = false)
	private ZonedDateTime fechaInscripcion;

	@Column(name = "foto_url", columnDefinition = "TEXT")
	private String fotoUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Rol rol = Rol.USER;

	/* Modelo de IA preferido — opcional */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "ia_modelo_seleccionado_id", nullable = true)
	private IaModelo iaModeloSeleccionado;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "usuario")
	private Set<Receta> recetas;

	/**
	 * Preferencias de tipos de comida del usuario.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "usuario_preferencia", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "tipo_comida_id"))
	private Set<TipoComida> preferencias;

	@ManyToMany
	@JoinTable(name = "usuario_amigo", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "amigo_id"))
	private Set<Usuario> amigos;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Set<UsuarioRecompensa> misRecompensas;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Set<Comentario> comentarios;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Set<FavoritoReceta> favoritosRecetas;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Set<FavoritoSupermercado> favoritosSupermercados;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Set<UsuarioLogro> misLogros;

	@PrePersist
	protected void onCreate() {
		// Solo asigna la fecha automática si el campo está vacío (es null)
		if (this.fechaInscripcion == null) {
			this.fechaInscripcion = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
		}
	}

}
