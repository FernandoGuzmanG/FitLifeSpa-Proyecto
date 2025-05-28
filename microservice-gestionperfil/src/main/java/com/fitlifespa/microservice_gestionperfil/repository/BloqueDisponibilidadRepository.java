package com.fitlifespa.microservice_gestionperfil.repository;

import com.fitlifespa.microservice_gestionperfil.model.BloqueDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface BloqueDisponibilidadRepository extends JpaRepository<BloqueDisponibilidad, Long> {
    List<BloqueDisponibilidad> findByIdUsuario(Long idUsuario);
}

