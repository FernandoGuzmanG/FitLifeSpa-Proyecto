package com.fitlifespa.microservice_clases.repository;

import com.fitlifespa.microservice_clases.model.EstadoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoInscripcionRepository extends JpaRepository<EstadoInscripcion, Long> {
    Optional<EstadoInscripcion> findByNombre(String nombre);
}
