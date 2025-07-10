package com.fitlifespa.microservice_tickets.controller;

import com.fitlifespa.microservice_tickets.model.Motivo;
import com.fitlifespa.microservice_tickets.model.Ticket;
import com.fitlifespa.microservice_tickets.service.TicketService;
import com.fitlifespa.microservice_tickets.security.RoleValidator;
import com.fitlifespa.microservice_tickets.hateoas.TicketModelAssembler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @InjectMocks
    private TicketController ticketController;

    @Mock
    private TicketService ticketService;

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private TicketModelAssembler ticketModelAssembler;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearTicket_Success() {
        Ticket entrada = new Ticket();
        entrada.setDescripcion("No funciona");
        Motivo motivo = new Motivo();
        motivo.setId(1L);
        entrada.setMotivo(motivo);

        Long idUsuario = 42L;

        Ticket creado = new Ticket();
        creado.setId(1L);

        EntityModel<Ticket> ticketModel = EntityModel.of(creado);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(ticketService.crearTicket("No funciona", idUsuario, 1L)).thenReturn(creado);
        when(ticketModelAssembler.toModel(creado)).thenReturn(ticketModel);

        ResponseEntity<EntityModel<Ticket>> response = ticketController.crearTicket(entrada, httpServletRequest);

        assertEquals(CREATED, response.getStatusCode());
        assertEquals(ticketModel, response.getBody());
        verify(ticketService).crearTicket("No funciona", idUsuario, 1L);
    }

    @Test
    void crearTicket_NotFound() {
        Ticket entrada = new Ticket();
        entrada.setDescripcion("No funciona");
        Motivo motivo = new Motivo();
        motivo.setId(1L);
        entrada.setMotivo(motivo);

        Long idUsuario = 42L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(ticketService.crearTicket("No funciona", idUsuario, 1L))
                .thenThrow(new RuntimeException("No se encontró motivo"));

        ResponseEntity<EntityModel<Ticket>> response = ticketController.crearTicket(entrada, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    void misTicketsCliente_Success() {
        Long idUsuario = 42L;
        Ticket t1 = new Ticket();
        Ticket t2 = new Ticket();

        EntityModel<Ticket> model1 = EntityModel.of(t1);
        EntityModel<Ticket> model2 = EntityModel.of(t2);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(ticketService.listarTicketsPorCliente(idUsuario)).thenReturn(List.of(t1, t2));
        when(ticketModelAssembler.toModel(t1)).thenReturn(model1);
        when(ticketModelAssembler.toModel(t2)).thenReturn(model2);

        ResponseEntity<?> response = ticketController.misTicketsCliente(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("EntityModel"));
        verify(ticketService).listarTicketsPorCliente(idUsuario);
    }

    @Test
    void misTicketsCliente_NoContent() {
        Long idUsuario = 42L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(ticketService.listarTicketsPorCliente(idUsuario)).thenReturn(List.of());

        ResponseEntity<?> response = ticketController.misTicketsCliente(httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
    }

    @Test
    void cambiarEstado_Success() {
        Long idTicket = 7L;
        String estado = "CERRADO";

        Ticket actualizado = new Ticket();
        actualizado.setId(idTicket);

        EntityModel<Ticket> model = EntityModel.of(actualizado);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(ticketService.cambiarEstado(idTicket, estado)).thenReturn(actualizado);
        when(ticketModelAssembler.toModel(actualizado)).thenReturn(model);

        ResponseEntity<EntityModel<Ticket>> response = ticketController.cambiarEstado(idTicket, estado, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(model, response.getBody());
        verify(ticketService).cambiarEstado(idTicket, estado);
    }
    @Test
    void misTicketsSoporte_Success() {
        Long idSoporte = 55L;
        Ticket t1 = new Ticket();
        t1.setId(1L);
        Ticket t2 = new Ticket();
        t2.setId(2L);

        EntityModel<Ticket> m1 = EntityModel.of(t1);
        EntityModel<Ticket> m2 = EntityModel.of(t2);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idSoporte);
        when(ticketService.listarTicketsPorSoporte(idSoporte)).thenReturn(List.of(t1, t2));
        when(ticketModelAssembler.toModel(t1)).thenReturn(m1);
        when(ticketModelAssembler.toModel(t2)).thenReturn(m2);

        ResponseEntity<?> response = ticketController.misTicketsSoporte(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("EntityModel"));
    }

    @Test
    void misTicketsSoporte_NoContent() {
        Long idSoporte = 55L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idSoporte);
        when(ticketService.listarTicketsPorSoporte(idSoporte)).thenReturn(List.of());

        ResponseEntity<?> response = ticketController.misTicketsSoporte(httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
    }

    @Test
    void verTicketsPorCliente_Success() {
        Long idCliente = 99L;
        Ticket t = new Ticket();
        t.setId(1L);

        EntityModel<Ticket> model = EntityModel.of(t);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(ticketService.listarTicketsPorCliente(idCliente)).thenReturn(List.of(t));
        when(ticketModelAssembler.toModel(t)).thenReturn(model);

        ResponseEntity<?> response = ticketController.verTicketsPorCliente(idCliente, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("EntityModel"));
    }

    @Test
    void verTicketsPorCliente_NoContent() {
        Long idCliente = 99L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(ticketService.listarTicketsPorCliente(idCliente)).thenReturn(List.of());

        ResponseEntity<?> response = ticketController.verTicketsPorCliente(idCliente, httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
    }

    @Test
    void verTicketsPorSoporte_Success() {
        Long idSoporte = 88L;
        Ticket t = new Ticket();
        t.setId(1L);

        EntityModel<Ticket> model = EntityModel.of(t);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(ticketService.listarTicketsPorSoporte(idSoporte)).thenReturn(List.of(t));
        when(ticketModelAssembler.toModel(t)).thenReturn(model);

        ResponseEntity<?> response = ticketController.verTicketsPorSoporte(idSoporte, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("EntityModel"));
    }

    @Test
    void verTicketsPorSoporte_NoContent() {
        Long idSoporte = 88L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(ticketService.listarTicketsPorSoporte(idSoporte)).thenReturn(List.of());

        ResponseEntity<?> response = ticketController.verTicketsPorSoporte(idSoporte, httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
    }

    @Test
    void asignar_Success() {
        Long idTicket = 10L;
        Long idSoporte = 77L;

        Ticket asignado = new Ticket();
        asignado.setId(idTicket);

        EntityModel<Ticket> model = EntityModel.of(asignado);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idSoporte);
        when(ticketService.asignarSoporte(idTicket, idSoporte)).thenReturn(asignado);
        when(ticketModelAssembler.toModel(asignado)).thenReturn(model);

        ResponseEntity<EntityModel<Ticket>> response = ticketController.asignar(idTicket, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(model, response.getBody());
    }

    @Test
    void asignar_NotFound() {
        Long idTicket = 10L;
        Long idSoporte = 77L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "SOPORTE");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idSoporte);
        when(ticketService.asignarSoporte(idTicket, idSoporte)).thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<EntityModel<Ticket>> response = ticketController.asignar(idTicket, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }


}
