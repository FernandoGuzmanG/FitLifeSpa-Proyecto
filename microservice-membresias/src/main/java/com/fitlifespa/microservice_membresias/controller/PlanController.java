package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
public class PlanController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private PlanService planService;

    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes() {
        List<Plan> planes = planService.findAll();
        if (planes.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(planes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlan(@PathVariable Long id) {
        return planService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Plan> crearPlan(@RequestBody Plan plan, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            plan.setIdPlan(null);
            Plan nuevoPlan = planService.save(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlan);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPlan(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        planService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

