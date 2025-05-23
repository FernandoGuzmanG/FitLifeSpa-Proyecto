package com.fitlifespa.membresia.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitlifespa.membresia.Model.Membresia;
import com.fitlifespa.membresia.Service.MembresiaService;

@RestController
@RequestMapping("/membresias")
public class MembresiaController {

    @Autowired
    private MembresiaService membresiaService;

    @GetMapping
    public ResponseEntity<List<Membresia>> findAll() {
        List<Membresia> membresias = membresiaService.findAll();
        if (membresias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membresias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Membresia> getMembresiaById(@PathVariable Long id) {
        Membresia membresia = membresiaService.findById(id);
        if (membresia != null) {
            return ResponseEntity.ok(membresia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/crear")
    public Membresia crearMembresia(@RequestBody Membresia membresia) {
        return membresiaService.createMembresia(membresia);
    }

    @PutMapping("/desactivar/{id}")
    public Membresia desactivarMembresia(@PathVariable Long id) {
        return membresiaService.desactivarMembresia(id);
    }

}