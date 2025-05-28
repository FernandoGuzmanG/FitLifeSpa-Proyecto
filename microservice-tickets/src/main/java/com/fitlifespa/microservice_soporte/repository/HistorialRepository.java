package com.fitlifespa.microservice_membresias.repository;

import com.fitlifespa.microservice_membresias.model.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {
    List<Historial> findByTicketIdOrderByFechaMensajeAsc(Long idTicket);
}
