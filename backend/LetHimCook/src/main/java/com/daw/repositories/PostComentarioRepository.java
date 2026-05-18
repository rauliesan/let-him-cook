package com.daw.repositories;

import com.daw.entities.PostComentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PostComentarioRepository extends JpaRepository<PostComentario, UUID> {

    List<PostComentario> findAllByPostIdOrderByFechaCreacionAsc(UUID postId);

    long countByUsuarioId(UUID usuarioId);
}
