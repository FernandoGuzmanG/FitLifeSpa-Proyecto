package com.fitlifespa.microservice_servicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlifespa.microservice_servicios.model.Servicio;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {}


