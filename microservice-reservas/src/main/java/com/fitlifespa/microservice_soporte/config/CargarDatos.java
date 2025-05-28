package com.fitlifespa.microservice_membresias.config;

import com.fitlifespa.microservice_membresias.model.EstadoReserva;
import com.fitlifespa.microservice_membresias.model.EstadoReservaHistorial;
import com.fitlifespa.microservice_membresias.model.Reserva;
import com.fitlifespa.microservice_membresias.repository.EstadoReservaHistorialRepository;
import com.fitlifespa.microservice_membresias.repository.EstadoReservaRepository;
import com.fitlifespa.microservice_membresias.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class CargarDatos {

    @Bean
    public CommandLineRunner precargarDatos(EstadoReservaRepository estadoRepo,
                                            ReservaRepository reservaRepo,
                                            EstadoReservaHistorialRepository historialRepo) {
        return args -> {
            // Precargar estados
            EstadoReserva pendiente = estadoRepo.save(new EstadoReserva(null, "PENDIENTE"));
            EstadoReserva confirmada = estadoRepo.save(new EstadoReserva(null, "CONFIRMADA"));
            EstadoReserva cancelada = estadoRepo.save(new EstadoReserva(null, "CANCELADA"));
            EstadoReserva completada = estadoRepo.save(new EstadoReserva(null, "COMPLETADA"));

            // Crear una reserva pendiente para cliente id 1
            Reserva r1 = new Reserva(null, LocalDate.now().plusDays(2), LocalDateTime.now(),
                    "Reserva de prueba", 1L, 1L, 3L, pendiente);
            reservaRepo.save(r1);
            historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now(), "Creada como prueba", pendiente, r1));

            // Crear una reserva completada para cliente id 2
            Reserva r2 = new Reserva(null, LocalDate.now().minusDays(3), LocalDateTime.now().minusDays(5),
                    "Membresía previa", 2L, 2L, 3L, completada);
            reservaRepo.save(r2);
            historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now().minusDays(5), "Reservado", pendiente, r2));
            historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now().minusDays(4), "Confirmado", confirmada, r2));
            historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now().minusDays(3), "Finalizado", completada, r2));

            // Crear una reserva cancelada para cliente id 1
            Reserva r3 = new Reserva(null, LocalDate.now().plusDays(1), LocalDateTime.now().minusDays(1),
                    "Cancelada por el usuario", 1L, 1L, 3L, cancelada);
            reservaRepo.save(r3);
            historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now().minusDays(1), "Creada", pendiente, r3));
            historialRepo.save(new EstadoReservaHistorial(null, LocalDateTime.now(), "Cancelada por usuario", cancelada, r3));
        };
    }
}


