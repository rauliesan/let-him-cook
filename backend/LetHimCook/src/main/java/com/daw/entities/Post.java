package com.daw.entities;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Post del foro — publicación que puede iniciar un hilo de conversación.
 * Los usuarios pueden compartir recetas, fotos o simplemente hablar.
 *
 * @author IES Almudeyne - Let Him Cook
 */
@Entity
@Table(name = "post")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    /* Imagen opcional adjunta al post */
    @Column(name = "imagen_url", columnDefinition = "TEXT")
    private String imagenUrl;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private ZonedDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /* Receta vinculada al post (opcional — un post puede no estar ligado a ninguna receta) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id")
    private Receta recetaVinculada;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<PostComentario> comentarios;

    /* Total de comentarios calculado con subconsulta */
    @Formula("(SELECT COUNT(*) FROM post_comentario pc WHERE pc.post_id = id)")
    private long totalComentarios;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
        }
    }
}
