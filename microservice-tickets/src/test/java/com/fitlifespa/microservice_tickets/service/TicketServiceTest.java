package com.fitlifespa.microservice_tickets.service;


import com.fitlifespa.microservice_tickets.model.EstadoTicket;
import com.fitlifespa.microservice_tickets.model.Motivo;
import com.fitlifespa.microservice_tickets.model.Ticket;
import com.fitlifespa.microservice_tickets.repository.EstadoTicketRepository;
import com.fitlifespa.microservice_tickets.repository.MotivoRepository;
import com.fitlifespa.microservice_tickets.repository.TicketRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepo;

    @Mock
    private MotivoRepository motivoRepo;

    @Mock
    private EstadoTicketRepository estadoRepo;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void testCrearTicketConDatosValidos() {
        Long idUsuario = 100L;
        Long idMotivo = 1L;
        String descripcion = "No puedo iniciar sesión";

        Motivo motivo = new Motivo(idMotivo, "Problema de acceso");
        EstadoTicket estado = new EstadoTicket(1L, "PENDIENTE");

        Ticket ticketGuardado = new Ticket();
        ticketGuardado.setId(1L);
        ticketGuardado.setDescripcion(descripcion);
        ticketGuardado.setIdUsuario(idUsuario);
        ticketGuardado.setMotivo(motivo);
        ticketGuardado.setEstado(estado);
        ticketGuardado.setFecha(LocalDateTime.now());

        when(motivoRepo.findById(idMotivo)).thenReturn(Optional.of(motivo));
        when(estadoRepo.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado));
        when(ticketRepo.save(any(Ticket.class))).thenReturn(ticketGuardado);

        Ticket resultado = ticketService.crearTicket(descripcion, idUsuario, idMotivo);

        assertNotNull(resultado);
        assertEquals(descripcion, resultado.getDescripcion());
        assertEquals(idUsuario, resultado.getIdUsuario());
        assertEquals(motivo, resultado.getMotivo());
        assertEquals(estado, resultado.getEstado());

        verify(motivoRepo).findById(idMotivo);
        verify(estadoRepo).findByNombre("PENDIENTE");
        verify(ticketRepo).save(any(Ticket.class));
    }

    @Test
    void testListarTicketsPorClienteDebeRetornarLista() {
        Long idUsuario = 1L;

        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);
        ticket1.setIdUsuario(idUsuario);
        ticket1.setFecha(LocalDateTime.now());

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setIdUsuario(idUsuario);
        ticket2.setFecha(LocalDateTime.now().minusHours(1));

        List<Ticket> lista = List.of(ticket1, ticket2);

        when(ticketRepo.findByIdUsuarioOrderByFechaDesc(idUsuario)).thenReturn(lista);

        List<Ticket> resultado = ticketService.listarTicketsPorCliente(idUsuario);

        assertEquals(2, resultado.size());
        assertEquals(ticket1.getId(), resultado.get(0).getId());
        verify(ticketRepo).findByIdUsuarioOrderByFechaDesc(idUsuario);
    }
    @Test
    void testAsignarSoporteDebeAsignarCorrectamente() {
        Long idTicket = 10L;
        Long idSoporte = 99L;

        Ticket ticketExistente = new Ticket();
        ticketExistente.setId(idTicket);
        ticketExistente.setIdSoporte(null);

        Ticket ticketActualizado = new Ticket();
        ticketActualizado.setId(idTicket);
        ticketActualizado.setIdSoporte(idSoporte);

        when(ticketRepo.findById(idTicket)).thenReturn(Optional.of(ticketExistente));
        when(ticketRepo.save(ticketExistente)).thenReturn(ticketActualizado);

        Ticket resultado = ticketService.asignarSoporte(idTicket, idSoporte);

        assertEquals(idSoporte, resultado.getIdSoporte());
        verify(ticketRepo).findById(idTicket);
        verify(ticketRepo).save(ticketExistente);
    }

    @Test
    void testCambiarEstadoDebeActualizarCorrectamente() {
        Long idTicket = 1L;
        String nuevoEstado = "EN PROCESO";

        Ticket ticket = new Ticket();
        ticket.setId(idTicket);
        ticket.setEstado(new EstadoTicket(1L, "PENDIENTE"));

        EstadoTicket nuevo = new EstadoTicket(2L, nuevoEstado);

        when(ticketRepo.findById(idTicket)).thenReturn(Optional.of(ticket));
        when(estadoRepo.findByNombre(nuevoEstado)).thenReturn(Optional.of(nuevo));
        when(ticketRepo.save(ticket)).thenReturn(ticket);

        Ticket resultado = ticketService.cambiarEstado(idTicket, nuevoEstado);

        assertEquals(nuevoEstado, resultado.getEstado().getNombre());
        verify(ticketRepo).findById(idTicket);
        verify(estadoRepo).findByNombre(nuevoEstado);
        verify(ticketRepo).save(ticket);
    }

    @Test
    void testCambiarEstadoDebeLanzarExcepcionSiTicketNoExiste() {
        when(ticketRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ticketService.cambiarEstado(1L, "CERRADO"));

        assertEquals("Ticket no encontrado", ex.getMessage());
    }

    @Test
    void testCambiarEstadoDebeLanzarExcepcionSiEstadoNoExiste() {
        Long idTicket = 1L;
        when(ticketRepo.findById(idTicket)).thenReturn(Optional.of(new Ticket()));
        when(estadoRepo.findByNombre("INVALIDO")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ticketService.cambiarEstado(idTicket, "INVALIDO"));

        assertEquals("Estado inválido", ex.getMessage());
    }



}

