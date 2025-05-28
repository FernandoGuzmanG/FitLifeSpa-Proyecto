package com.fitlifespa.microservice_servicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlifespa.microservice_servicios.model.EstadoServicio;

import java.util.Optional;

@Repository
public interface EstadoServicioRepository extends JpaRepository<EstadoServicio, Long> {
    Optional<EstadoServicio> findByNombre(String nombre);
}
