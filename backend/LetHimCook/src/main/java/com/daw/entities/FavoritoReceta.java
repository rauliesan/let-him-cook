package com.daw.entities;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Clase para gestionar las recetas favoritas de los usuarios.
 *
 * @author IES Almudeyne - Raúl Liébana Sánchez
 */
@Entity
@Table(name = "favorito_receta")
@Data
public class FavoritoReceta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Usuario que seleccionó como favorita la receta.
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /**
     * Receta que ha sido seleccionada como favorita.
     */
    @ManyToOne
    @JoinColumn(name = "receta_id")
    private Receta receta;

    @Column(name = "fecha_agregada")
    private ZonedDateTime fechaAgregada;
}