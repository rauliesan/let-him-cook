package com.daw.entities;

import java.time.ZonedDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "favorito_receta")
@Data
public class FavoritoReceta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "receta_id")
    private Receta receta;

    @CreationTimestamp
    @Column(name = "fecha_agregada")
    private ZonedDateTime fechaAgregada;
}