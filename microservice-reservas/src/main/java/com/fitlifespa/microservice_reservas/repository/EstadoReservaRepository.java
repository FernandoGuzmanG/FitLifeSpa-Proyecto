package com.fitlifespa.microservice_reservas.repository;

import com.fitlifespa.microservice_reservas.model.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, Long> {
    Optional<EstadoReserva> findByNombre(String nombre);
}