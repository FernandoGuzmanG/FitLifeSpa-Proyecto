package com.fitlifespa.membresia.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlifespa.membresia.Model.Plan;
import com.fitlifespa.membresia.Service.PlanService;

@RestController
@RequestMapping("/planes")
public class PlanController {
    private PlanService planService;
    @Autowired
    @GetMapping
    public List<Plan> obtenerTodos() {
        return planService.findAll();
    }

    @GetMapping("/{id}")
    public Plan obtenerPorId(@PathVariable Long id) {
        return planService.findById(id);
    }

    @PostMapping("/planes")
    public Plan crearPlan(@RequestBody Plan plan) {
        return planService.save(plan);
    }
}
