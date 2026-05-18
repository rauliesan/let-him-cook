package com.daw.entities;

import java.time.ZoneId;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "comentario")
@Data
public class Comentario {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String comentario;

	@Column(name = "valoracion")
	private Integer valoracion; // 1 a 5

	@Column(name = "fecha_creacion", nullable = false)
	private ZonedDateTime fechaCreacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receta_id", nullable = false)
	private Receta receta;
	
	
	
	@PrePersist
	protected void onCreate() {
	    // Solo asigna la fecha automática si el campo está vacío (es null)
	    if (this.fechaCreacion == null) {
	        this.fechaCreacion = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
	    }
	}

}
