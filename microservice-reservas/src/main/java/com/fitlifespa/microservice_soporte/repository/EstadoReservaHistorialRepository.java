package com.fitlifespa.microservice_membresias.repository;

import com.fitlifespa.microservice_membresias.model.EstadoReservaHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoReservaHistorialRepository extends JpaRepository<EstadoReservaHistorial, Long> {
    List<EstadoReservaHistorial> findByReservaIdOrderByFechaCambioDesc(Long idReserva);
}
