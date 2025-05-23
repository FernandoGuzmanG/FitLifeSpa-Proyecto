package com.fitlifespa.membresia.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlifespa.membresia.Model.Incluido;
import com.fitlifespa.membresia.Service.IncluidoService;

@RestController
@RequestMapping("/incluidos")
public class IncluidoController {
    private IncluidoService incluidoService;

    @Autowired
    @GetMapping
    public ResponseEntity<List<Incluido>> obtenerTodos() {
        List<Incluido> incluidos = incluidoService.getAllIncluido();
        if (incluidos.isEmpty()) {
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.ok(incluidos);
    }
    
    @GetMapping("/plan/{idPlan}")
    public ResponseEntity<List<Incluido>> obtenerPorPlan(@PathVariable Long idPlan) {
        List<Incluido> incluidos = incluidoService.findByPlan(idPlan);
        if (incluidos.isEmpty()) {
            throw new RuntimeException("No se encontraron servicios incluidos para el plan con ID: " + idPlan);
        }
        return ResponseEntity.ok(incluidos);
    }
}
