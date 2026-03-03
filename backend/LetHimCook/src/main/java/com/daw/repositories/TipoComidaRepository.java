package com.daw.repositories;

import com.daw.entities.TipoComida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TipoComidaRepository extends JpaRepository<TipoComida, UUID> {
}