package com.daw.repositories;

import com.daw.entities.Receta;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.entities.Dificultad;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, UUID> {
    Page<Receta> findByNombreContainingIgnoreCaseAndDificultad(
            String nombre, Dificultad dificultad, Pageable pageable);

    boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);
}