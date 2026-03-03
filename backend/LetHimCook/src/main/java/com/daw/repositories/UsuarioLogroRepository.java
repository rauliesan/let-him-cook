package com.daw.repositories;

import com.daw.entities.UsuarioLogro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UsuarioLogroRepository extends JpaRepository<UsuarioLogro, UUID> {
}