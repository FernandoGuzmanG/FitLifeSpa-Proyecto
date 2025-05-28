package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.model.Membresia;
import com.fitlifespa.microservice_membresias.service.MembresiaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/membresias")
public class MembresiaController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private MembresiaService membresiaService;

    @GetMapping
    public ResponseEntity<List<Membresia>> listarMembresias(HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Membresia> membresias = membresiaService.findAll();
        if (membresias.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membresias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Membresia> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        return membresiaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Membresia>> listarPorUsuario(@PathVariable Long idUsuario, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Membresia> membresias = membresiaService.findAllByIdUser(idUsuario);
        if (membresias.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membresias);
    }

    @GetMapping("/mis-membresias")
    public ResponseEntity<List<Membresia>> listarMembresiasCliente(HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));
        List<Membresia> membresias = membresiaService.findAllByIdUser(idUsuario);
        if (membresias.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(membresiaService.findAllByIdUser(idUsuario));
    }

    @PostMapping
    public ResponseEntity<Membresia> crearMembresia(@RequestBody Membresia membresia, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR", "CLIENTE");
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaTermino = fechaInicio.plusDays(30);
        try{
            membresia.setIdMembresia(null);
            membresia.setFechaInicio(fechaInicio);
            membresia.setFechaTermino(fechaTermino);
            Membresia nuevaMembresia = membresiaService.save(membresia);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMembresia);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/renovar/{id}")
    public ResponseEntity<Membresia> renovar(@PathVariable String id, HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));
        Long idMembresia = Long.parseLong(id);
        Membresia renovada = membresiaService.renovarMembresia(idUsuario, idMembresia);
        return ResponseEntity.ok(renovada);
    }


}

