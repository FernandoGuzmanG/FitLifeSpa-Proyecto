package com.fitlifespa.microservice_resenas.repository;

import com.fitlifespa.microservice_resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByIdServicio(Long idServicio);
    List<Resena> findByIdUsuario(Long idUsuario);
}

