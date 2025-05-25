package com.fitlife.servicios.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fitlife.servicios.Model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    @Query(value = "SELECT * FROM rol WHERE nombre_rol = :nombreRol", nativeQuery = true)
    Rol findByNombre(@Param("nombreRol") String nombreRol);

}
