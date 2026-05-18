package com.daw.repositories;

import com.daw.entities.Recompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RecompensaRepository extends JpaRepository<Recompensa, UUID> {
    boolean existsByNombre(String nombre);
}