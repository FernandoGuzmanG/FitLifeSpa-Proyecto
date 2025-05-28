package com.fitlifespa.microservice_auth.repository;

import com.fitlifespa.microservice_auth.model.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, Long> {
    Optional<EstadoUsuario> findByNombreEstado(String nombreEstado);
}
