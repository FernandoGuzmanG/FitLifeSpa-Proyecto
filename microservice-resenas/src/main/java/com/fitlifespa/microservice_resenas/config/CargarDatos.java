package com.fitlifespa.microservice_resenas.config;

import com.fitlifespa.microservice_resenas.model.Resena;
import com.fitlifespa.microservice_resenas.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CargarDatos {

    @Bean
    public CommandLineRunner precargarResenas(ResenaRepository resenaRepo) {
        return args -> {
            if (resenaRepo.count() == 0) {
                List<Resena> resenas = List.of(
                        new Resena(null, LocalDate.now().minusDays(10), "Muy buen servicio, el entrenador fue muy atento.", 2L, 1L, 9.0),
                        new Resena(null, LocalDate.now().minusDays(7), "Instalaciones limpias y bien mantenidas.", 2L, 2L, 8.5),
                        new Resena(null, LocalDate.now().minusDays(3), "Tuve problemas con la reserva, pero el personal me ayudó.", 2L, 1L, 7.0),
                        new Resena(null, LocalDate.now().minusDays(1), "Excelente experiencia, definitivamente volveré.", 2L, 2L, 10.0)
                );

                resenaRepo.saveAll(resenas);
                System.out.println("Reseñas precargadas correctamente.");
            }
        };
    }
}

