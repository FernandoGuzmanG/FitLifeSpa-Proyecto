package com.fitlifespa.microservice_membresias.repository;

import com.fitlifespa.microservice_membresias.model.EstadoTicket;
import com.fitlifespa.microservice_membresias.model.Motivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotivoRepository extends JpaRepository<Motivo, Long> {
    Optional<Motivo> findByDescripcion(String descripcion);

}
