package com.daw.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.entities.Supermercado;

@Repository
public interface SupermercadoRepository extends JpaRepository<Supermercado, UUID> {
    Page<Supermercado> findByNombreContainingIgnoreCaseAndDireccionContainingIgnoreCase(
            String nombre, String direccion, Pageable pageable);
}