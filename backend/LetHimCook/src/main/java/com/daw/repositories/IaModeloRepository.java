package com.daw.repositories;

import com.daw.entities.IaModelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface IaModeloRepository extends JpaRepository<IaModelo, UUID> {
}