package com.fitlifespa.microservice_membresias.repository;

import com.fitlifespa.microservice_membresias.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByIdUsuarioOrderByFechaInicioDesc(Long idUsuario);
    Optional<Membresia> findTopByIdUsuarioOrderByFechaInicioDesc(Long idUsuario);
}

