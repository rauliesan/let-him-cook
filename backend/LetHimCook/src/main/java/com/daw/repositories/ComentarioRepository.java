package com.daw.repositories;

import com.daw.entities.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, UUID> {
    Page<Comentario> findByRecetaId(UUID recetaId, Pageable pageable);

    boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);
}