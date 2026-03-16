package com.daw.repositories;

import com.daw.entities.UsuarioLogro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Repository
public interface UsuarioLogroRepository extends JpaRepository<UsuarioLogro, UUID> {
    Page<UsuarioLogro> findByUsuarioId(UUID usuarioId, Pageable pageable);

    boolean existsByUsuarioIdAndLogroId(UUID usuarioId, UUID logroId);

}