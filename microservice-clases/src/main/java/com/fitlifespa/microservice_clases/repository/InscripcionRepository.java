package com.fitlifespa.microservice_clases.repository;

import com.fitlifespa.microservice_clases.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByIdUser(Long idUser);
    List<Inscripcion> findByClase_IdClase(Long idClase);
    List<Inscripcion> findByIdUserAndEstado_Nombre(Long idUser, String estado);
}


