package com.fitlifespa.microservice_gestionperfil.repository;

import com.fitlifespa.microservice_gestionperfil.model.DireccionEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionEnvioRepository extends JpaRepository<DireccionEnvio, Long> {
    List<DireccionEnvio> findByIdUsuario(Long idUsuario);
}