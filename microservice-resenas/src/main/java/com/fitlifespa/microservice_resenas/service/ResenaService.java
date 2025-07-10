package com.fitlifespa.microservice_resenas.service;

import com.fitlifespa.microservice_resenas.model.Enum.EstadoResena;
import com.fitlifespa.microservice_resenas.model.Resena;
import com.fitlifespa.microservice_resenas.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;

    public Resena crear(Long idUsuario, Long idServicio, String comentario, Double calificacion) {
        if (calificacion < 1.0 || calificacion > 10.0) {
            throw new IllegalArgumentException("La calificación debe estar entre 1.0 y 10.0");
        }

        if (comentario.length() > 500){
            throw new IllegalArgumentException("El comentario no puede superar los 500 carácteres");
        }

        Resena resena = new Resena();
        resena.setIdUsuario(idUsuario);
        resena.setIdServicio(idServicio);
        resena.setFechaResena(LocalDate.now());
        resena.setComentario(comentario);
        resena.setEstado(EstadoResena.DESBANEADO);
        resena.setCalificacion(calificacion);
        return resenaRepository.save(resena);
    }

    public Double obtenerPromedioCalificacionPorServicio(Long idServicio, EstadoResena estado) {
        List<Resena> resenas = resenaRepository.findByIdServicioAndEstado(idServicio, estado);

        if (resenas.isEmpty()) return 0.0;

        return resenas.stream()
                .mapToDouble(Resena::getCalificacion)
                .average()
                .orElse(0.0);
    }


    public List<Resena> obtenerPorServicio(Long idServicio, EstadoResena estado) {
        return resenaRepository.findByIdServicioAndEstado(idServicio, estado);
    }

    public List<Resena> obtenerPorUsuario(Long idUsuario) {
        return resenaRepository.findByIdUsuario(idUsuario);
    }

    public Resena buscarPorEstadoId(EstadoResena estado, Long id) {
        return resenaRepository.findByEstadoAndId(estado, id);
    }

    public List<Resena> buscarPorEstado(EstadoResena estado) {
        return resenaRepository.findByEstado(estado);
    }

    public Resena buscarResenaUsuario(Long idUsuario, Long idResena){
        return resenaRepository.findByIdUsuarioAndId(idUsuario, idResena);
    }


    public Resena desbanear(Long id) {
        Resena resena = resenaRepository.findByEstadoAndId(EstadoResena.BANEADO, id);
        resena.setEstado(EstadoResena.DESBANEADO);
        return resenaRepository.save(resena);
    }

    public Resena banear(Long id) {
        Resena resena = resenaRepository.findByEstadoAndId(EstadoResena.DESBANEADO, id);
        resena.setEstado(EstadoResena.BANEADO);
        return resenaRepository.save(resena);
    }

    public Resena modificarResena(Long idUsuario, Long idResena, Resena nuevosDatos) {
        Resena resena = resenaRepository.findByIdUsuarioAndId(idUsuario, idResena);

        if (nuevosDatos.getComentario().length() > 500) {
            throw new IllegalArgumentException("El comentario no puede superar los 500 caracteres.");
        }

        if (nuevosDatos.getCalificacion() < 1.0 || nuevosDatos.getCalificacion() > 10.0) {
            throw new IllegalArgumentException("La calificación debe estar entre 1.0 y 10.0.");
        }

        resena.setComentario(nuevosDatos.getComentario());
        resena.setCalificacion(nuevosDatos.getCalificacion());

        return resenaRepository.save(resena);
    }

    public void eliminarResenaUsuario(Long idUsuario, Long idResena) {
        Resena resena = buscarResenaUsuario(idUsuario, idResena);
        resenaRepository.deleteById(resena.getId());
    }



}

