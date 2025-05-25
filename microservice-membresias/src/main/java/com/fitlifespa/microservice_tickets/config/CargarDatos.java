package com.fitlifespa.microservice_tickets.config;

import com.fitlifespa.microservice_tickets.model.Incluido;
import com.fitlifespa.microservice_tickets.model.Membresia;
import com.fitlifespa.microservice_tickets.model.Plan;
import com.fitlifespa.microservice_tickets.repository.IncluidoRepository;
import com.fitlifespa.microservice_tickets.repository.MembresiaRepository;
import com.fitlifespa.microservice_tickets.repository.PlanRepository;
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
    public CommandLineRunner seedData(
            PlanRepository planRepo,
            IncluidoRepository incluidoRepo,
            MembresiaRepository membresiaRepo
    ) {
        return args -> {
            Plan planBasico = new Plan(null, "Plan Básico", "Acceso general", 19900.0, 30, null);
            Plan planPremium = new Plan(null, "Plan Premium", "Acceso total + clases", 29900.0, 30, null);
            planRepo.saveAll(List.of(planBasico, planPremium));

            incluidoRepo.saveAll(List.of(
                    new Incluido(null, 1L, planBasico),
                    new Incluido(null, 2L, planPremium),
                    new Incluido(null, 3L, planPremium)
            ));

            Membresia activa = new Membresia(null, LocalDate.now(), LocalDate.now().plusDays(30), 2L, planPremium.getCosto(), planPremium);
            Membresia expirada = new Membresia(null, LocalDate.now().minusDays(60), LocalDate.now().minusDays(30), 2L, planPremium.getCosto(), planBasico);
            membresiaRepo.saveAll(List.of(activa, expirada));
        };
    }
}
