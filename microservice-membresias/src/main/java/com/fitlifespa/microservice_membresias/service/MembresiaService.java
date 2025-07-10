package com.fitlifespa.microservice_membresias.service;

import com.fitlifespa.microservice_membresias.model.Membresia;
import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.repository.MembresiaRepository;
import com.fitlifespa.microservice_membresias.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {
    @Autowired
    private MembresiaRepository membresiaRepository;
    @Autowired
    private PlanRepository planRepository;

    public List<Membresia> findAll(){
        return membresiaRepository.findAll();
    }

    public List<Membresia> findAllByIdUser(Long idUsuario) {
        return membresiaRepository.findByIdUsuarioOrderByFechaInicioDesc(idUsuario) ;
    }

    public Optional<Membresia> findById(Long id) {
        return membresiaRepository.findById(id);
    }

    public Membresia crearMembresia(Membresia membresia, Long idCliente) {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaTermino = fechaInicio.plusDays(30);
        membresia.setIdUsuario(idCliente);
        membresia.setIdMembresia(null);
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaTermino(fechaTermino);
        membresiaRepository.save(membresia);
        return membresia;
    }

    public Membresia renovarMembresia(Long idMembresia, Long idUsuario) {
        Membresia membresiaActual = membresiaRepository.findById(idMembresia)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));

        if (!membresiaActual.getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("La membresía no pertenece al usuario");
        }

        Plan plan = membresiaActual.getPlan();

        LocalDate nuevaFechaInicio = membresiaActual.getFechaTermino().plusDays(plan.getDuracion());
        LocalDate nuevaFechaTermino = nuevaFechaInicio.plusMonths(plan.getDuracion());

        Membresia nuevaMembresia = new Membresia();
        nuevaMembresia.setFechaInicio(nuevaFechaInicio);
        nuevaMembresia.setFechaTermino(nuevaFechaTermino);
        nuevaMembresia.setIdUsuario(idUsuario);
        nuevaMembresia.setPlan(plan);
        nuevaMembresia.setCostoTotal(plan.getCosto());

        return membresiaRepository.save(nuevaMembresia);
    }


    public void cancelarMembresia(Long idMembresia, Long idUsuario) {
        Membresia membresia = membresiaRepository.findById(idMembresia)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));

        if (!membresia.getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("La membresía no pertenece al usuario");
        }

        if (membresia.getFechaInicio().plusDays(7).isBefore(LocalDate.now())) {
            throw new RuntimeException("La membresía ya no puede ser cancelada");
        }

        membresiaRepository.delete(membresia);
    }

    public Membresia cambiarPlanMembresia(Long idMembresia, Long idUsuario, Long nuevoIdPlan) {
        Membresia membresia = membresiaRepository.findById(idMembresia)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));

        if (!membresia.getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("La membresía no pertenece al usuario");
        }

        if (membresia.getFechaTermino().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se puede cambiar el plan de una membresía expirada");
        }

        Plan nuevoPlan = planRepository.findById(nuevoIdPlan)
                .orElseThrow(() -> new RuntimeException("El nuevo plan no existe"));

        membresia.setPlan(nuevoPlan);
        membresia.setCostoTotal(nuevoPlan.getCosto());

        return membresiaRepository.save(membresia);
    }






}
