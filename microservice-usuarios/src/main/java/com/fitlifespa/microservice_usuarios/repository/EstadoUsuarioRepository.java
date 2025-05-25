package com.fitlifespa.microservice_usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fitlifespa.microservice_usuarios.model.EstadoUsuario;

@Repository
public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, Long>{
    @Query(value = "SELECT * FROM estado_usuario WHERE nombre_estado = :nombreEstado", nativeQuery = true)
    EstadoUsuario findByNombre(@Param("nombreEstado") String nombreEstado);
}