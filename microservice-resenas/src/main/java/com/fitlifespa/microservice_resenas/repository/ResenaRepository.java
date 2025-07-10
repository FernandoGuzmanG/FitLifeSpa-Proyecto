package com.fitlifespa.microservice_resenas.repository;

import com.fitlifespa.microservice_resenas.model.Enum.EstadoResena;
import com.fitlifespa.microservice_resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByIdServicioAndEstado(Long idServicio, EstadoResena estado);
    List<Resena> findByIdUsuario(Long idUsuario);
    List<Resena> findByEstado(EstadoResena estado);
    Resena findByEstadoAndId(EstadoResena estado, Long id);
    Resena findByIdUsuarioAndId(Long idUsuario, Long idResena);
}

