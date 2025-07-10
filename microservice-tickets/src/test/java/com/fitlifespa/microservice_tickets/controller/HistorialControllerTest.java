package com.fitlifespa.microservice_tickets.controller;

import com.fitlifespa.microservice_tickets.model.Historial;
import com.fitlifespa.microservice_tickets.service.HistorialService;
import com.fitlifespa.microservice_tickets.security.RoleValidator;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class HistorialControllerTest {

    @InjectMocks
    private HistorialController historialController;

    @Mock
    private HistorialService historialService;

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void responder_Success_Soporte() {
        Long idTicket = 5L;
        String mensaje = "Revisado y resuelto";
        String rolHeader = "SOPORTE";

        Historial historialMock = new Historial();

        when(httpServletRequest.getHeader("X-User-Roles")).thenReturn(rolHeader);
        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE", "SOPORTE");
        when(historialService.agregarEntrada(idTicket, "SOPORTE", mensaje)).thenReturn(historialMock);

        ResponseEntity<EntityModel<Historial>> response = historialController.responder(idTicket, mensaje, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(historialMock, response.getBody().getContent());
        verify(roleValidator).requireRole(httpServletRequest, "CLIENTE", "SOPORTE");
        verify(historialService).agregarEntrada(idTicket, "SOPORTE", mensaje);
    }

    @Test
    void responder_Success_Cliente() {
        Long idTicket = 5L;
        String mensaje = "Gracias por la ayuda";
        String rolHeader = "CLIENTE";

        Historial historialMock = new Historial();

        when(httpServletRequest.getHeader("X-User-Roles")).thenReturn(rolHeader);
        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE", "SOPORTE");
        when(historialService.agregarEntrada(idTicket, "CLIENTE", mensaje)).thenReturn(historialMock);

        ResponseEntity<EntityModel<Historial>> response = historialController.responder(idTicket, mensaje, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(historialMock, response.getBody().getContent());
        verify(roleValidator).requireRole(httpServletRequest, "CLIENTE", "SOPORTE");
        verify(historialService).agregarEntrada(idTicket, "CLIENTE", mensaje);
    }

    @Test
    void historial_Success() {
        Long idTicket = 5L;

        Historial h1 = new Historial();
        Historial h2 = new Historial();

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE", "SOPORTE");
        when(historialService.listarPorTicket(idTicket)).thenReturn(List.of(h1, h2));

        ResponseEntity<CollectionModel<EntityModel<Historial>>> response =
                historialController.historial(idTicket, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());

        verify(roleValidator).requireRole(httpServletRequest, "CLIENTE", "SOPORTE");
        verify(historialService).listarPorTicket(idTicket);
    }

    @Test
    void historial_NoContent() {
        Long idTicket = 5L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE", "SOPORTE");
        when(historialService.listarPorTicket(idTicket)).thenReturn(List.of());

        ResponseEntity<CollectionModel<EntityModel<Historial>>> response =
                historialController.historial(idTicket, httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
    }

}
