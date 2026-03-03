package com.daw.repositories;

import com.daw.entities.Supermercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SupermercadoRepository extends JpaRepository<Supermercado, UUID> {
}