package com.fitlifespa.microservice_resenas.service;

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

    private final ResenaRepository resenaRepo;

    public Resena crear(Long idUsuario, Long idServicio, String comentario, Double calificacion) {
        if (calificacion < 1.0 || calificacion > 10.0) {
            throw new IllegalArgumentException("La calificación debe estar entre 1.0 y 10.0");
        }

        Resena resena = new Resena();
        resena.setIdUsuario(idUsuario);
        resena.setIdServicio(idServicio);
        resena.setFechaResena(LocalDate.now());
        resena.setComentario(comentario);
        resena.setCalificacion(calificacion);
        return resenaRepo.save(resena);
    }

    public Double obtenerPromedioCalificacionPorServicio(Long idServicio) {
        List<Resena> resenas = resenaRepo.findByIdServicio(idServicio);

        if (resenas.isEmpty()) return 0.0;

        return resenas.stream()
                .mapToDouble(Resena::getCalificacion)
                .average()
                .orElse(0.0);
    }


    public List<Resena> obtenerPorServicio(Long idServicio) {
        return resenaRepo.findByIdServicio(idServicio);
    }

    public List<Resena> obtenerPorUsuario(Long idUsuario) {
        return resenaRepo.findByIdUsuario(idUsuario);
    }

    public Optional<Resena> obtenerPorId(Long id) {
        return resenaRepo.findById(id);
    }


}

