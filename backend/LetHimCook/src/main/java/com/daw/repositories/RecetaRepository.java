package com.daw.repositories;

import com.daw.entities.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, UUID> {
}