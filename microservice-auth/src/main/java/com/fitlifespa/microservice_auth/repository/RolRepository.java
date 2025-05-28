package com.fitlifespa.microservice_auth.repository;

import com.fitlifespa.microservice_auth.model.Enum.NombreRol;
import com.fitlifespa.microservice_auth.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreRol(NombreRol nombreRol);
}
