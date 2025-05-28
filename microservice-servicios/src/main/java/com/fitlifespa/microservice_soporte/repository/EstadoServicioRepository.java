package com.fitlifespa.microservice_membresias.repository;

import com.fitlifespa.microservice_membresias.model.EstadoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoServicioRepository extends JpaRepository<EstadoServicio, Long> {
    Optional<EstadoServicio> findByNombre(String nombre);
}
