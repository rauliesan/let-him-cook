package com.daw.repositories;

import com.daw.entities.FavoritoSupermercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FavoritoSupermercadoRepository extends JpaRepository<FavoritoSupermercado, UUID> {
}