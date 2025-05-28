package com.fitlifespa.microservice_clases.service;
import com.fitlifespa.microservice_clases.model.Clase;
import com.fitlifespa.microservice_clases.model.EstadoClase;
import com.fitlifespa.microservice_clases.repository.ClaseRepository;
import com.fitlifespa.microservice_clases.repository.EstadoClaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final EstadoClaseRepository estadoClaseRepository;

    public Clase crearClase(String nombre, String descripcion, Long idServicio, Long idEntrenador, LocalDate fecha) {
        Clase clase = new Clase();
        clase.setNombres(nombre);
        clase.setDescripcion(descripcion);
        clase.setIdServicio(idServicio);
        clase.setIdUsuario(idEntrenador);
        clase.setFClase(fecha);
        clase.setEstado(estadoClaseRepository.findByNombre("ACTIVO").orElseThrow());

        return claseRepository.save(clase);
    }

    public Clase actualizar(Clase clase) {
        return claseRepository.save(clase);
    }

    public Clase cambiarEstado(Long id, String nuevoEstado) {
        Clase clase = claseRepository.findById(id).orElseThrow();
        EstadoClase estado = estadoClaseRepository.findByNombre(nuevoEstado).orElseThrow();
        clase.setEstado(estado);
        return claseRepository.save(clase);
    }

    public Optional<Clase> buscarPorId(Long id) {
        return claseRepository.findById(id);
    }

    public List<Clase> listarPorEntrenador(Long idEntrenador) {
        return claseRepository.findByIdUsuario(idEntrenador);
    }
}
