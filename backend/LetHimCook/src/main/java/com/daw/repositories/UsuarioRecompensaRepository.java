package com.daw.repositories;

import com.daw.entities.UsuarioRecompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UsuarioRecompensaRepository extends JpaRepository<UsuarioRecompensa, UUID> {
}