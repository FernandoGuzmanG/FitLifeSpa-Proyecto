package com.fitlifespa.microservice_reservas.controller;

import com.fitlifespa.microservice_reservas.model.Reserva;
import com.fitlifespa.microservice_reservas.service.ReservaService;
import com.fitlifespa.microservice_reservas.security.RoleValidator;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class ReservaControllerTest {

    @InjectMocks
    private ReservaController reservaController;

    @Mock
    private ReservaService reservaService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearReserva_Success() {
        LocalDate fecha = LocalDate.now();
        String descripcion = "Reserva prueba";
        Long idServicio = 10L;
        Long idEntrenador = 5L;
        Long idUsuario = 42L;

        Reserva reservaMock = new Reserva();

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(reservaService.crear(fecha, descripcion, idUsuario, idServicio, idEntrenador)).thenReturn(reservaMock);

        ResponseEntity<Reserva> response = reservaController.crear(fecha, descripcion, idServicio, idEntrenador, httpServletRequest);

        assertEquals(CREATED, response.getStatusCode());
        assertEquals(reservaMock, response.getBody());

        verify(validator).requireRole(httpServletRequest, "CLIENTE");
        verify(validator).getUserId(httpServletRequest);
        verify(reservaService).crear(fecha, descripcion, idUsuario, idServicio, idEntrenador);
    }

    @Test
    void misReservas_Success() {
        Long idUsuario = 42L;
        List<Reserva> reservasMock = List.of(new Reserva(), new Reserva());

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(reservaService.listarPorUsuario(idUsuario)).thenReturn(reservasMock);

        ResponseEntity<List<Reserva>> response = reservaController.misReservas(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(validator).requireRole(httpServletRequest, "CLIENTE");
        verify(reservaService).listarPorUsuario(idUsuario);
    }

    @Test
    void misReservas_NoContent() {
        Long idUsuario = 42L;

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(reservaService.listarPorUsuario(idUsuario)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Reserva>> response = reservaController.misReservas(httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
    }

    @Test
    void asignadas_Success() {
        Long idEntrenador = 7L;
        List<Reserva> reservasMock = List.of(new Reserva(), new Reserva());

        doNothing().when(validator).requireRole(httpServletRequest, "ENTRENADOR");
        when(validator.getUserId(httpServletRequest)).thenReturn(idEntrenador);
        when(reservaService.listarPorEntrenador(idEntrenador)).thenReturn(reservasMock);

        ResponseEntity<List<Reserva>> response = reservaController.asignadas(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(validator).requireRole(httpServletRequest, "ENTRENADOR");
        verify(reservaService).listarPorEntrenador(idEntrenador);
    }

    @Test
    void cancelarReserva_Success() {
        Long idReserva = 5L;
        Reserva reservaMock = new Reserva();

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(reservaService.cambiarEstado(idReserva, "CANCELADA", "Cancelado por cliente")).thenReturn(reservaMock);

        ResponseEntity<?> response = reservaController.cancelar(idReserva, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(reservaMock, response.getBody());

        verify(validator).requireRole(httpServletRequest, "CLIENTE");
        verify(reservaService).cambiarEstado(idReserva, "CANCELADA", "Cancelado por cliente");
    }

    @Test
    void cancelarReserva_NotFound() {
        Long idReserva = 5L;

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(reservaService.cambiarEstado(idReserva, "CANCELADA", "Cancelado por cliente"))
                .thenThrow(new RuntimeException("No encontrada"));

        ResponseEntity<?> response = reservaController.cancelar(idReserva, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }
}
