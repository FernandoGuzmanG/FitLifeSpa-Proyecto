package com.fitlifespa.gestionusuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fitlifespa.gestionusuarios.model.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long>{
    @Query(value = "SELECT * FROM estado WHERE nombre_estado = :nombreEstado", nativeQuery = true)
    Estado findByNombre(@Param("nombreEstado") String nombreEstado);
}
