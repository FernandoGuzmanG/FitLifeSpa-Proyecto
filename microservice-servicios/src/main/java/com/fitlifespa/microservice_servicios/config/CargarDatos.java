package com.fitlifespa.microservice_servicios.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fitlifespa.microservice_servicios.model.EstadoServicio;
import com.fitlifespa.microservice_servicios.model.Servicio;
import com.fitlifespa.microservice_servicios.repository.EstadoServicioRepository;
import com.fitlifespa.microservice_servicios.repository.ServicioRepository;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CargarDatos {

    @Bean
    public CommandLineRunner initEstados(EstadoServicioRepository estadoRepo, ServicioRepository servicioRepo) {
        return args -> {
            if (estadoRepo.count() == 0) {
                estadoRepo.saveAll(List.of(
                        new EstadoServicio(null, "ACTIVO"),
                        new EstadoServicio(null, "INACTIVO")
                ));
            }
            if (servicioRepo.count() == 0) {
                EstadoServicio activo = estadoRepo.findByNombre("ACTIVO").orElseThrow();

                List<Servicio> servicios = List.of(
                        new Servicio(null, "Masaje relajante", "Masaje de cuerpo completo para aliviar tensiones", activo),
                        new Servicio(null, "Asesoría nutricional", "Consulta personalizada con nutricionista", activo),
                        new Servicio(null, "Entrenamiento personalizado", "Rutina personalizada con un entrenador", activo)
                );

                servicioRepo.saveAll(servicios);
                System.out.println("Servicios precargados correctamente.");
            }
        };
    }
}
