package com.daw.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.daw.entities.Rol;
import com.daw.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNombre(String nombre);

    boolean existsByEmail(String email);

    boolean existsByNombreIgnoreCase(String nombre);

    Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    long countByRol(Rol rol);

    @Query("SELECT COUNT(r) > 0 FROM Usuario u JOIN u.recetasCompletadas r WHERE u.id = :uid AND r.id = :rid")
    boolean haCompletadoReceta(@Param("uid") UUID uid, @Param("rid") UUID rid);

    @Query("SELECT SIZE(u.recetasCompletadas) FROM Usuario u WHERE u.id = :uid")
    int countRecetasCompletadas(@Param("uid") UUID uid);

    @Query("SELECT SIZE(u.amigos) FROM Usuario u WHERE u.id = :uid")
    int countAmigos(@Param("uid") UUID uid);
}
