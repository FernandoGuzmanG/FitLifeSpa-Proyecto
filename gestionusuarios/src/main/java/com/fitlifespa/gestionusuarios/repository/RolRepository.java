package com.fitlifespa.gestionusuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlifespa.gestionusuarios.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

}
