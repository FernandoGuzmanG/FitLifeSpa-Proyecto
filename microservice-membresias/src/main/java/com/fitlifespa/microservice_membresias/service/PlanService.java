package com.fitlifespa.microservice_membresias.service;

import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public List<Plan> findAll() {
        return planRepository.findAll();
    }

    public Optional<Plan> findById(Long id) {
        return planRepository.findById(id);
    }

    public Plan actualizarPlan(Long idPlan, Plan datosActualizados) {
        Plan planExistente = planRepository.findById(idPlan)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        // Actualizar campos permitidos
        planExistente.setNombre(datosActualizados.getNombre());
        planExistente.setDescripcion(datosActualizados.getDescripcion());
        planExistente.setCosto(datosActualizados.getCosto());
        planExistente.setDuracion(datosActualizados.getDuracion());

        return planRepository.save(planExistente);
    }


    public Plan crearPlan(Plan plan) {
        plan.setIdPlan(null);
        return planRepository.save(plan);
    }

    public void deleteById(Long id) {
        planRepository.deleteById(id);
    }
}
