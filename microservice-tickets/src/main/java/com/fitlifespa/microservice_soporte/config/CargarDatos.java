package com.fitlifespa.microservice_membresias.config;

import com.fitlifespa.microservice_membresias.model.EstadoTicket;
import com.fitlifespa.microservice_membresias.model.Historial;
import com.fitlifespa.microservice_membresias.model.Motivo;
import com.fitlifespa.microservice_membresias.model.Ticket;
import com.fitlifespa.microservice_membresias.repository.EstadoTicketRepository;
import com.fitlifespa.microservice_membresias.repository.HistorialRepository;
import com.fitlifespa.microservice_membresias.repository.MotivoRepository;
import com.fitlifespa.microservice_membresias.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CargarDatos {

    @Bean
    public CommandLineRunner runner(
            MotivoRepository motivoRepo,
            EstadoTicketRepository estadoRepo,
            TicketRepository ticketRepo,
            HistorialRepository historialRepo
    ) {
        return args -> {
            // Crear motivos si no existen
            List<String> motivos = List.of("Fallo del sistema", "Consulta general", "Solicitud de mejora");
            for (String descripcion : motivos) {
                if (motivoRepo.findByDescripcion(descripcion).isEmpty()) {
                    motivoRepo.save(new Motivo(null, descripcion));
                }
            }

            // Crear estados si no existen
            List<String> estados = List.of("PENDIENTE", "EN_PROGRESO", "CERRADO");
            for (String nombre : estados) {
                if (estadoRepo.findByNombre(nombre).isEmpty()) {
                    estadoRepo.save(new EstadoTicket(null, nombre));
                }
            }

            // Precargar un ticket con respuestas
            if (ticketRepo.count() == 0) {
                Motivo motivo = motivoRepo.findByDescripcion("Fallo del sistema").orElseThrow();
                EstadoTicket estado = estadoRepo.findByNombre("EN_PROGRESO").orElseThrow();

                Ticket ticket = new Ticket();
                ticket.setFecha(LocalDateTime.now().minusDays(2));
                ticket.setMotivo(motivo);
                ticket.setIdUsuario(2L); // cliente
                ticket.setIdUsuarioRol(3L); // soporte
                ticket.setEstado(estado);
                Ticket ticketGuardado = ticketRepo.save(ticket);

                // Crear historial de respuestas
                List<Historial> historial = List.of(
                        new Historial(null, "Cliente", "Tengo un problema al iniciar sesión", LocalDateTime.now().minusDays(2), ticketGuardado),
                        new Historial(null, "Soporte", "¿Ha intentado restablecer la contraseña?", LocalDateTime.now().minusDays(1), ticketGuardado),
                        new Historial(null, "Cliente", "Sí, y aún así no me deja", LocalDateTime.now().minusHours(12), ticketGuardado),
                        new Historial(null, "Soporte", "Procederemos a resetear manualmente su cuenta", LocalDateTime.now(), ticketGuardado)
                );

                historialRepo.saveAll(historial);
            }
        };
    }
}


