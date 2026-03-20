package com.daw.repositories;

import com.daw.entities.FavoritoSupermercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Repository
public interface FavoritoSupermercadoRepository extends JpaRepository<FavoritoSupermercado, UUID> {
    Page<FavoritoSupermercado> findByUsuarioId(UUID usuarioId, Pageable pageable);

    boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);
}