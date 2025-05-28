package com.fitlifespa.microservice_servicios.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fitlifespa.microservice_servicios.model.EstadoServicio;
import com.fitlifespa.microservice_servicios.model.Servicio;
import com.fitlifespa.microservice_servicios.repository.EstadoServicioRepository;
import com.fitlifespa.microservice_servicios.repository.ServicioRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepo;
    private final EstadoServicioRepository estadoRepo;

    public Servicio crear(String nombre, String descripcion) {
        EstadoServicio estado = estadoRepo.findByNombre("ACTIVO")
                .orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));

        Servicio s = new Servicio();
        s.setNombre(nombre);
        s.setDescripcion(descripcion);
        s.setEstado(estado);
        return servicioRepo.save(s);
    }

    public List<Servicio> listarTodos() {
        return servicioRepo.findAll();
    }

    public List<Servicio> listarActivos() {
        return servicioRepo.findAll().stream()
                .filter(s -> "ACTIVO".equalsIgnoreCase(s.getEstado().getNombre()))
                .toList();
    }

    public Servicio actualizar(Long id, String nombre, String descripcion) {
        Servicio s = servicioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        s.setNombre(nombre);
        s.setDescripcion(descripcion);
        return servicioRepo.save(s);
    }

    public Servicio desactivarServicio(Long id) {
        Servicio servicio = servicioRepo.findById(id).orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        EstadoServicio estado = estadoRepo.findByNombre("INACTIVO").orElseThrow(() -> new RuntimeException("Estado no válido"));
        servicio.setEstado(estado);
        return servicioRepo.save(servicio);
    }

    public Servicio activarServicio(Long id) {
        Servicio servicio = servicioRepo.findById(id).orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        EstadoServicio estado = estadoRepo.findByNombre("ACTIVO").orElseThrow(() -> new RuntimeException("Estado no válido"));
        servicio.setEstado(estado);
        return servicioRepo.save(servicio);
    }

    public Optional<Servicio> obtenerPorId(Long id) {
        return servicioRepo.findById(id);
    }
}

