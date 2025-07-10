package com.fitlifespa.microservice_reservas.service;

import com.fitlifespa.microservice_reservas.model.EstadoReserva;
import com.fitlifespa.microservice_reservas.model.EstadoReservaHistorial;
import com.fitlifespa.microservice_reservas.model.Reserva;
import com.fitlifespa.microservice_reservas.repository.EstadoReservaHistorialRepository;
import com.fitlifespa.microservice_reservas.repository.EstadoReservaRepository;
import com.fitlifespa.microservice_reservas.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepo;

    @Mock
    private EstadoReservaRepository estadoRepo;

    @Mock
    private EstadoReservaHistorialRepository historialRepo;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crear_deberiaCrearReservaYGuardarHistorial() {
        // Arrange
        LocalDate fecha = LocalDate.of(2025, 7, 1);
        String descripcion = "Evaluación";
        Long idUsuario = 1L;
        Long idServicio = 2L;
        Long idEntrenador = 3L;

        EstadoReserva pendiente = new EstadoReserva(1L, "PENDIENTE");

        Reserva reservaGuardada = new Reserva(10L, fecha, LocalDateTime.now(), descripcion, idUsuario, idServicio, idEntrenador, pendiente);

        when(estadoRepo.findByNombre("PENDIENTE")).thenReturn(Optional.of(pendiente));
        when(reservaRepo.save(any(Reserva.class))).thenReturn(reservaGuardada);

        // Act
        Reserva result = reservaService.crear(fecha, descripcion, idUsuario, idServicio, idEntrenador);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("PENDIENTE", result.getEstado().getNombre());

        verify(estadoRepo).findByNombre("PENDIENTE");
        verify(reservaRepo).save(any(Reserva.class));
        verify(historialRepo).save(any(EstadoReservaHistorial.class));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiEstadoNoExiste() {
        when(estadoRepo.findByNombre("PENDIENTE")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                reservaService.crear(LocalDate.now(), "desc", 1L, 2L, 3L)
        );

        verify(estadoRepo).findByNombre("PENDIENTE");
        verify(reservaRepo, never()).save(any());
        verify(historialRepo, never()).save(any());
    }

    @Test
    void cambiarEstado_deberiaActualizarEstadoYGuardarHistorial() {
        Long idReserva = 5L;
        String nuevoEstado = "CONFIRMADA";
        String comentario = "Confirmado por entrenador";

        EstadoReserva estadoNuevo = new EstadoReserva(2L, "CONFIRMADA");
        Reserva reserva = new Reserva(idReserva, LocalDate.now(), LocalDateTime.now(), "desc", 1L, 2L, 3L, new EstadoReserva());

        when(reservaRepo.findById(idReserva)).thenReturn(Optional.of(reserva));
        when(estadoRepo.findByNombre(nuevoEstado)).thenReturn(Optional.of(estadoNuevo));
        when(reservaRepo.save(reserva)).thenReturn(reserva);

        Reserva result = reservaService.cambiarEstado(idReserva, nuevoEstado, comentario);

        assertEquals("CONFIRMADA", result.getEstado().getNombre());
        verify(historialRepo).save(any(EstadoReservaHistorial.class));
    }


    @Test
    void cambiarEstado_deberiaLanzarExcepcionSiReservaNoExiste() {
        when(reservaRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                reservaService.cambiarEstado(99L, "CANCELADA", "comentario")
        );

        verify(reservaRepo).findById(99L);
        verify(estadoRepo, never()).findByNombre(any());
    }


    @Test
    void listarPorUsuario_deberiaRetornarReservas() {
        Long idUsuario = 1L;
        when(reservaRepo.findByIdUsuarioOrderByFechaDesc(idUsuario)).thenReturn(List.of(new Reserva()));
        List<Reserva> resultado = reservaService.listarPorUsuario(idUsuario);
        assertEquals(1, resultado.size());
    }

    @Test
    void listarPorEntrenador_deberiaRetornarReservas() {
        Long idEntrenador = 1L;
        when(reservaRepo.findByIdEntrenadorOrderByFechaDesc(idEntrenador)).thenReturn(List.of(new Reserva()));
        List<Reserva> resultado = reservaService.listarPorEntrenador(idEntrenador);
        assertEquals(1, resultado.size());
    }

    @Test
    void historial_deberiaRetornarHistorialDeReserva() {
        Long idReserva = 5L;
        when(historialRepo.findByReservaIdOrderByFechaCambioDesc(idReserva)).thenReturn(List.of(new EstadoReservaHistorial()));
        List<EstadoReservaHistorial> resultado = reservaService.historial(idReserva);
        assertEquals(1, resultado.size());
    }

}

