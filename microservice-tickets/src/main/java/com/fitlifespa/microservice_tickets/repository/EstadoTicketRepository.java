package com.fitlifespa.microservice_tickets.repository;

import com.fitlifespa.microservice_tickets.model.EstadoTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoTicketRepository extends JpaRepository<EstadoTicket, Long> {
    Optional<EstadoTicket> findByNombre(String nombreEstado);
}
