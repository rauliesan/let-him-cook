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

    @Column(name = "fecha_obtenido")
    private ZonedDateTime fechaObtenido;
    
    
    @PrePersist
	protected void onCreate() {
	    // Solo asigna la fecha automática si el campo está vacío (es null)
	    if (this.fechaObtenido == null) {
	        this.fechaObtenido = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
	    }
	}
}