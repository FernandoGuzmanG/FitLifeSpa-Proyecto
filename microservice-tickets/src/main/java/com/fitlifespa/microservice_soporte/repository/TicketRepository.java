package com.fitlifespa.microservice_membresias.repository;

import com.fitlifespa.microservice_membresias.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByIdUsuarioOrderByFechaDesc(Long idUsuario);
}
