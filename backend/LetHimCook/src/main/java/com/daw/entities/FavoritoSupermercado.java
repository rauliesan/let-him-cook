package com.daw.entities;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Clase para gestionar los supermercados favoritos de los usuarios.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "favorito_supermercado")
@Data
public class FavoritoSupermercado {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Usuario que seleccionó el supermercado como favorito.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /**
     * Supermercado seleccionado como favorito.
     */
    @ManyToOne
    @JoinColumn(name = "supermercado_id")
    private Supermercado supermercado;

    @Column(name = "fecha_agregada")
    private ZonedDateTime fechaAgregada;
    
    
    @PrePersist
	protected void onCreate() {
	    // Solo asigna la fecha automática si el campo está vacío (es null)
	    if (this.fechaAgregada == null) {
	        this.fechaAgregada = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
	    }
	}
}