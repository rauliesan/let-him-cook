package com.daw.repositories;

import com.daw.entities.FavoritoReceta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface FavoritoRecetaRepository extends JpaRepository<FavoritoReceta, UUID> {

    Page<FavoritoReceta> findByUsuarioId(UUID usuarioId, Pageable pageable);

    List<FavoritoReceta> findAllByUsuarioId(UUID usuarioId);

    boolean existsByUsuarioIdAndRecetaId(UUID usuarioId, UUID recetaId);

    boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);

    void deleteByUsuarioIdAndRecetaId(UUID usuarioId, UUID recetaId);

    long countByRecetaId(UUID recetaId);

    long countByUsuarioId(UUID usuarioId);
}
