package com.daw.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

}
