package com.daw.repositories;

import com.daw.entities.FavoritoReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FavoritoRecetaRepository extends JpaRepository<FavoritoReceta, UUID> {
}