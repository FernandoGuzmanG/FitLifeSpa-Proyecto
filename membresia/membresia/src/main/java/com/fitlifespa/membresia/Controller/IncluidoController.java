package com.fitlifespa.membresia.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlifespa.membresia.Model.Incluido;
import com.fitlifespa.membresia.Service.IncluidoService;

@RestController
@RequestMapping("/api/incluido")
public class IncluidoController {
    private IncluidoService incluidoService;

    @Autowired
    @GetMapping("/incluidos")
    public ResponseEntity<List<Incluido>> obtenerTodos() {
        List<Incluido> incluidos = incluidoService.getAllIncluido();
        if (incluidos.isEmpty()) {
            return ResponseEntity.noContent().build(); 
        }
        return ResponseEntity.ok(incluidos);
    }
    
    @GetMapping("/incluido/id/{id}")
    public ResponseEntity<?> mostrarIncluido(@PathVariable Long id){
        try{
            Incluido incluido = incluidoService.findById(id);
            return ResponseEntity.ok(incluido);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/incluido/id/{id}/delte")
    public ResponseEntity<?> eliminarIncluido(@PathVariable Long id){
        try{
            incluidoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
        
    }
}
