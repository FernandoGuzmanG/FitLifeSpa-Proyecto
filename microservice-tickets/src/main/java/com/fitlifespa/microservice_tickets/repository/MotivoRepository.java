package com.fitlifespa.microservice_tickets.repository;

import com.fitlifespa.microservice_tickets.model.Motivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotivoRepository extends JpaRepository<Motivo, Long> {
}
