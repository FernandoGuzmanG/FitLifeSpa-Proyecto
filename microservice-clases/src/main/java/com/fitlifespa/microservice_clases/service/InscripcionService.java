package com.fitlifespa.microservice_clases.service;

import com.fitlifespa.microservice_clases.model.Clase;
import com.fitlifespa.microservice_clases.model.EstadoInscripcion;
import com.fitlifespa.microservice_clases.model.Inscripcion;
import com.fitlifespa.microservice_clases.repository.ClaseRepository;
import com.fitlifespa.microservice_clases.repository.EstadoInscripcionRepository;
import com.fitlifespa.microservice_clases.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final ClaseRepository claseRepository;
    private final EstadoInscripcionRepository estadoInscripcionRepository;

    public Inscripcion inscribirse(Long idUsuario, Long idClase) {
        Clase clase = claseRepository.findById(idClase).orElseThrow();

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setFInscripcion(LocalDate.now());
        inscripcion.setIdUser(idUsuario);
        inscripcion.setClase(clase);
        inscripcion.setEstado(estadoInscripcionRepository.findByNombre("INSCRITO").orElseThrow());

        return inscripcionRepository.save(inscripcion);
    }

    public List<Inscripcion> inscripcionesDeUsuario(Long idUsuario) {
        return inscripcionRepository.findByIdUser(idUsuario);
    }

    public List<Inscripcion> inscritosEnClase(Long idClase) {
        return inscripcionRepository.findByClase_IdClase(idClase);
    }

    public Inscripcion cancelarInscripcion(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id).orElseThrow();
        EstadoInscripcion cancelado = estadoInscripcionRepository.findByNombre("CANCELADO").orElseThrow();
        inscripcion.setEstado(cancelado);
        return inscripcionRepository.save(inscripcion);
    }

    public List<Inscripcion> historialFinalizadas(Long idUsuario) {
        return inscripcionRepository.findByIdUserAndEstado_Nombre(idUsuario, "FINALIZADO");
    }
}

