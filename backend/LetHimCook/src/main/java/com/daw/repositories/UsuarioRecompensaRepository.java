package com.daw.repositories;

import com.daw.entities.UsuarioRecompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Repository
public interface UsuarioRecompensaRepository extends JpaRepository<UsuarioRecompensa, UUID> {
    Page<UsuarioRecompensa> findByUsuarioId(UUID usuarioId, Pageable pageable);

    boolean existsByUsuarioIdAndRecompensaId(UUID usuarioId, UUID recompensaId);

    long countByUsuarioId(UUID usuarioId);
}