package com.fitlifespa.microservice_tickets.controller;

import com.fitlifespa.microservice_tickets.model.Ticket;
import com.fitlifespa.microservice_tickets.security.RoleValidator;
import com.fitlifespa.microservice_tickets.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RoleValidator roleValidator;

    @PostMapping("/crear")
    public ResponseEntity<Ticket> crearTicket(@RequestBody Ticket ticket, HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = roleValidator.getUserId(request);
        try{
            Ticket nuevoTicket = ticketService.crearTicket(ticket.getDescripcion(), idUsuario, ticket.getMotivo().getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTicket);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/mis-tickets")
    public ResponseEntity<List<Ticket>> misTickets(HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = roleValidator.getUserId(request);
        List<Ticket> tickets = ticketService.listarTicketsPorUsuario(idUsuario);
        if (tickets.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Ticket>> listarTodos(HttpServletRequest request) {
        roleValidator.requireRole(request, "SOPORTE");
        List<Ticket> tickets = ticketService.listarTodos();
        if (tickets.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}/estado/{estado}")
    public ResponseEntity<Ticket> cambiarEstado(@PathVariable Long id, @PathVariable String estado, HttpServletRequest request) {
        roleValidator.requireRole(request, "SOPORTE");
        try{
            String estado1 = estado.toUpperCase();
            return ResponseEntity.ok(ticketService.cambiarEstado(id, estado1));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/asignar")
    public ResponseEntity<Ticket> asignar(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "SOPORTE");
        Long idSoporte = roleValidator.getUserId(request);
        try{
            return ResponseEntity.ok(ticketService.asignarSoporte(id, idSoporte));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
