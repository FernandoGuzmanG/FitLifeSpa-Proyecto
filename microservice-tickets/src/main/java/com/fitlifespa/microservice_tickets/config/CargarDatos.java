package com.fitlifespa.microservice_tickets.config;

import com.fitlifespa.microservice_tickets.model.EstadoTicket;
import com.fitlifespa.microservice_tickets.model.Motivo;
import com.fitlifespa.microservice_tickets.repository.EstadoTicketRepository;
import com.fitlifespa.microservice_tickets.repository.MotivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CargarDatos {

    @Bean
    public CommandLineRunner runner(
            MotivoRepository motivoRepo,
            EstadoTicketRepository estadoRepo
    ) {
        return args -> {
            motivoRepo.saveAll(List.of(
                    new Motivo(null, "Fallo del sistema"),
                    new Motivo(null, "Consulta general"),
                    new Motivo(null, "Solicitud de mejora")
            ));

            estadoRepo.saveAll(List.of(
                    new EstadoTicket(null, "PENDIENTE"),
                    new EstadoTicket(null, "EN_PROGRESO"),
                    new EstadoTicket(null, "CERRADO")
            ));
        };
    }
}

