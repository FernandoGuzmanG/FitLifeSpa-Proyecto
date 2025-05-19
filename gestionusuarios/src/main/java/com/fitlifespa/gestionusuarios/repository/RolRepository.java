package com.fitlifespa.gestionusuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fitlifespa.gestionusuarios.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    @Query(value = "SELECT * FROM rol WHERE nombre_rol = :nombreRol", nativeQuery = true)
    Rol findByNombre(@Param("nombreRol") String nombreRol);
}
