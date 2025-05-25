package com.fitlifespa.membresia.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fitlifespa.membresia.Model.Plan;
import com.fitlifespa.membresia.Service.PlanService;

@RestController
@RequestMapping("/plan")
public class PlanController {
    private PlanService planService;
    
    @Autowired
    @GetMapping("/planes")
    public ResponseEntity<List<Plan>> mostrarPlanes() {
        List<Plan> plan = planService.findAll();
        if (plan.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/plan/id/{id}")
    public ResponseEntity<?> getPlanById(@PathVariable Long id) {
        try {
            Plan plan = planService.findById(id);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/planes")
    public ResponseEntity<Plan> createPlan(@RequestBody Plan plan) {
        try{
            plan.setIdPlan(null);
            Plan nuevoPlan = planService.save(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlan);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

}
