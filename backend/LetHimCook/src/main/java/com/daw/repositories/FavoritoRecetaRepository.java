package com.daw.repositories;

import com.daw.entities.FavoritoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Repository
public interface FavoritoRecetaRepository extends JpaRepository<FavoritoReceta, UUID> {
    Page<FavoritoReceta> findByUsuarioId(UUID usuarioId, Pageable pageable);

    boolean existsByUsuarioIdAndRecetaId(UUID usuarioId, UUID recetaId);

    boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);
}