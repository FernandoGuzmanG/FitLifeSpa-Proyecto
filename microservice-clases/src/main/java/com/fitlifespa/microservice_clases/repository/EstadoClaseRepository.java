package com.fitlifespa.microservice_clases.repository;

import com.fitlifespa.microservice_clases.model.EstadoClase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoClaseRepository extends JpaRepository<EstadoClase, Long> {
    Optional<EstadoClase> findByNombre(String nombre);
}

