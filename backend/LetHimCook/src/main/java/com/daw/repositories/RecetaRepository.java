package com.daw.repositories;

import com.daw.entities.Receta;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.entities.Dificultad;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, UUID> {
    Page<Receta> findByNombreContainingIgnoreCaseAndDificultad(
            String nombre, Dificultad dificultad, Pageable pageable);

    @Query("SELECT r FROM Receta r WHERE " +
           "(LOWER(r.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(r.ingredientes) LIKE LOWER(CONCAT('%', :termino, '%'))) AND " +
           "(:dificultad IS NULL OR r.dificultad = :dificultad)")
    Page<Receta> buscarDinamico(@Param("termino") String termino, 
                               @Param("dificultad") Dificultad dificultad, 
                               Pageable pageable);

    boolean existsByIdAndUsuarioId(UUID id, UUID usuarioId);
}