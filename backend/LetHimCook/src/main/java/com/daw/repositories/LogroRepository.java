package com.daw.repositories;

import com.daw.entities.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface LogroRepository extends JpaRepository<Logro, UUID> {
}