package com.fitlifespa.microservice_clases.config;

import com.fitlifespa.microservice_clases.model.Clase;
import com.fitlifespa.microservice_clases.model.EstadoClase;
import com.fitlifespa.microservice_clases.model.EstadoInscripcion;
import com.fitlifespa.microservice_clases.model.Inscripcion;
import com.fitlifespa.microservice_clases.repository.ClaseRepository;
import com.fitlifespa.microservice_clases.repository.EstadoClaseRepository;
import com.fitlifespa.microservice_clases.repository.EstadoInscripcionRepository;
import com.fitlifespa.microservice_clases.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CargarDatos implements CommandLineRunner {

    private final EstadoClaseRepository estadoClaseRepository;
    private final EstadoInscripcionRepository estadoInscripcionRepository;

    @Override
    public void run(String... args) {
        if (estadoClaseRepository.count() == 0) {
            estadoClaseRepository.saveAll(List.of(
                    new EstadoClase(null, "ACTIVO"),
                    new EstadoClase(null, "INACTIVO"),
                    new EstadoClase(null, "FINALIZADO")
            ));
        }

        if (estadoInscripcionRepository.count() == 0) {
            estadoInscripcionRepository.saveAll(List.of(
                    new EstadoInscripcion(null, "INSCRITO"),
                    new EstadoInscripcion(null, "CANCELADO"),
                    new EstadoInscripcion(null, "FINALIZADO")
            ));
        }
    }
}
