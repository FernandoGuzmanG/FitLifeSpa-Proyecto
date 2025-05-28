package com.fitlifespa.microservice_membresias.repository;

import com.fitlifespa.microservice_membresias.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {}


