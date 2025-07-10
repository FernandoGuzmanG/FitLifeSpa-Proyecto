package com.fitlifespa.microservice_tickets.repository;

import com.fitlifespa.microservice_tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByIdUsuarioOrderByFechaDesc(Long idUsuario);
    List<Ticket> findByIdSoporteOrderByFechaDesc(Long idSoporte);

}
