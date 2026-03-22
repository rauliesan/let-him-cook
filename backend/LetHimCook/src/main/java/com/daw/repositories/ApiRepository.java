package com.daw.repositories;

import com.daw.entities.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ApiRepository extends JpaRepository<Api, UUID> {
    boolean existsByEndpointUrl(String endpointUrl);
}