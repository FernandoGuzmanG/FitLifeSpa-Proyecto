package com.fitlifespa.membresia.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private MembresiaService membresiaService;

    @Autowired
    @GetMapping("/membresias")
    public ResponseEntity<List<Membresia>> findAll() {
        List<Membresia> membresias = membresiaService.findAll();
        if (membresias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membresias);
    }

    @GetMapping("/membresias/id/{id}")
    public ResponseEntity<?> getMembresiaById(@PathVariable Long id) {
        try {
            Membresia membresia = membresiaService.findById(id);
            return ResponseEntity.ok(membresia);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/membresia")
    public ResponseEntity<Membresia> registrarMembresia(@RequestBody Membresia membresia) {
        try{
            membresia.setIdMembresia(null);
            Membresia nuevaMembresia = membresiaService.save(membresia);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMembresia);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/membresia/id/{id}")
    public ResponseEntity<Membresia> actualizarMembresia(@RequestBody Membresia membresia, @PathVariable Long id) {
        try{
            Membresia mem = membresiaService.findById(id);
            mem.setPnombre(membresia.getPnombre());
            mem.setSnombre(membresia.getSnombre());
            mem.setAppaterno(membresia.getAppaterno());
            mem.setApmaterno(mem.getApmaterno());

            return ResponseEntity.ok(mem);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    



}
