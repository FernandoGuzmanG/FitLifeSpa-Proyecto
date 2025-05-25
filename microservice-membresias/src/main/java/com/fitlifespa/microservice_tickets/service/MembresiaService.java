package com.fitlifespa.microservice_tickets.service;

import com.fitlifespa.microservice_tickets.model.Membresia;
import com.fitlifespa.microservice_tickets.model.Plan;
import com.fitlifespa.microservice_tickets.repository.MembresiaRepository;
import com.fitlifespa.microservice_tickets.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private MembresiaRepository membresiaRepository;

    public List<Membresia> findAll(){
        return membresiaRepository.findAll();
    }

    public List<Membresia> findAllByIdUser(Long idUsuario) {
        return membresiaRepository.findByIdUsuarioOrderByFechaInicioDesc(idUsuario);
    }

    public Optional<Membresia> findById(Long id) {
        return membresiaRepository.findById(id);
    }

    public Optional<Membresia> obtenerMembresiaActual(Long idUsuario) {
        return membresiaRepository.findTopByIdUsuarioOrderByFechaInicioDesc(idUsuario);
    }

    public Membresia save(Membresia membresia) {
        return membresiaRepository.save(membresia);
    }

    public void deleteById(Long id) {
        membresiaRepository.deleteById(id);
    }

    public Membresia renovarMembresia(Long idUsuario, Long idPlan, Long idMembresia) {
        Plan plan = planRepository.findById(idPlan)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        LocalDate nuevaFechaInicio = LocalDate.now();

        Optional<Membresia> ultima = membresiaRepository.findTopByIdUsuarioOrderByFechaInicioDesc(idUsuario);
        if (ultima.isPresent() && ultima.get().getFechaTermino().isAfter(nuevaFechaInicio)) {
            nuevaFechaInicio = ultima.get().getFechaTermino(); // renueva al terminar la actual
        }

        Membresia nueva = new Membresia();
        nueva.setIdUsuario(idUsuario);
        nueva.setPlan(plan);
        nueva.setFechaInicio(nuevaFechaInicio);
        nueva.setFechaTermino(nuevaFechaInicio.plusDays(plan.getDuracion()));
        nueva.setCostoTotal(plan.getCosto());

        return membresiaRepository.save(nueva);
    }

}
