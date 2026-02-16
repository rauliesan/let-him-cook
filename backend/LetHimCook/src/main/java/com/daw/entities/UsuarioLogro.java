package com.daw.entities;

import java.time.ZonedDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "usuario_logro")
@Data
public class UsuarioLogro {
	
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "logro_id")
    private Logro logro;

    @CreationTimestamp
    @Column(name = "fecha_obtenido")
    private ZonedDateTime fechaObtenido;
}