package com.fitlifespa.membresia.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlifespa.membresia.Model.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
    Optional<Estado> findByNombre(String nombre);



}
