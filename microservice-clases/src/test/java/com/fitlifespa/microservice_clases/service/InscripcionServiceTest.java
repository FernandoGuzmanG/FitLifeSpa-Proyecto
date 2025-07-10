package com.fitlifespa.microservice_clases.service;

import com.fitlifespa.microservice_clases.model.Clase;
import com.fitlifespa.microservice_clases.model.EstadoInscripcion;
import com.fitlifespa.microservice_clases.model.Inscripcion;
import com.fitlifespa.microservice_clases.repository.ClaseRepository;
import com.fitlifespa.microservice_clases.repository.EstadoInscripcionRepository;
import com.fitlifespa.microservice_clases.repository.InscripcionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private ClaseRepository claseRepository;

    @Mock
    private EstadoInscripcionRepository estadoInscripcionRepository;

    @InjectMocks
    private InscripcionService inscripcionService;

    @Test
    void inscribirse_deberiaCrearInscripcionCorrectamente() {
        // Arrange
        Long idUsuario = 100L;
        Long idClase = 10L;

        Clase clase = new Clase();
        clase.setIdClase(idClase);

        EstadoInscripcion estadoInscrito = new EstadoInscripcion(1L, "INSCRITO");

        Inscripcion inscripcionGuardada = new Inscripcion(
                1L,
                LocalDate.now(),
                idUsuario,
                clase,
                estadoInscrito
        );

        when(claseRepository.findById(idClase)).thenReturn(Optional.of(clase));
        when(estadoInscripcionRepository.findByNombre("INSCRITO")).thenReturn(Optional.of(estadoInscrito));
        when(inscripcionRepository.save(any(Inscripcion.class))).thenReturn(inscripcionGuardada);

        // Act
        Inscripcion resultado = inscripcionService.inscribirse(idUsuario, idClase);

        // Assert
        assertNotNull(resultado);
        assertEquals(idUsuario, resultado.getIdUser());
        assertEquals(idClase, resultado.getClase().getIdClase());
        assertEquals("INSCRITO", resultado.getEstado().getNombre());

        verify(claseRepository).findById(idClase);
        verify(estadoInscripcionRepository).findByNombre("INSCRITO");
        verify(inscripcionRepository).save(any(Inscripcion.class));
    }

    @Test
    void inscribirse_deberiaLanzarExcepcionSiClaseNoExiste() {
        // Arrange
        Long idClase = 99L;
        when(claseRepository.findById(idClase)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            inscripcionService.inscribirse(1L, idClase);
        });

        verify(claseRepository).findById(idClase);
        verify(estadoInscripcionRepository, never()).findByNombre(any());
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void inscribirse_deberiaLanzarExcepcionSiEstadoNoExiste() {
        // Arrange
        Long idUsuario = 101L;
        Long idClase = 20L;

        Clase clase = new Clase();
        clase.setIdClase(idClase);

        when(claseRepository.findById(idClase)).thenReturn(Optional.of(clase));
        when(estadoInscripcionRepository.findByNombre("INSCRITO")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            inscripcionService.inscribirse(idUsuario, idClase);
        });

        verify(claseRepository).findById(idClase);
        verify(estadoInscripcionRepository).findByNombre("INSCRITO");
        verify(inscripcionRepository, never()).save(any());
    }
    @Test
    void inscritosEnClase_deberiaRetornarListaDeInscripciones() {
        // Arrange
        Long idClase = 10L;
        List<Inscripcion> inscripciones = List.of(
                new Inscripcion(1L, LocalDate.now(), 100L, new Clase(), new EstadoInscripcion())
        );

        when(inscripcionRepository.findByClase_IdClase(idClase)).thenReturn(inscripciones);

        // Act
        List<Inscripcion> resultado = inscripcionService.inscritosEnClase(idClase);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(inscripcionRepository).findByClase_IdClase(idClase);
    }
    @Test
    void cancelarInscripcion_deberiaActualizarEstadoACancelado() {
        // Arrange
        Long idInscripcion = 5L;
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdInscripcion(idInscripcion);

        EstadoInscripcion cancelado = new EstadoInscripcion(2L, "CANCELADO");

        when(inscripcionRepository.findById(idInscripcion)).thenReturn(Optional.of(inscripcion));
        when(estadoInscripcionRepository.findByNombre("CANCELADO")).thenReturn(Optional.of(cancelado));
        when(inscripcionRepository.save(inscripcion)).thenReturn(inscripcion);

        // Act
        Inscripcion resultado = inscripcionService.cancelarInscripcion(idInscripcion);

        // Assert
        assertNotNull(resultado);
        assertEquals("CANCELADO", resultado.getEstado().getNombre());

        verify(inscripcionRepository).findById(idInscripcion);
        verify(estadoInscripcionRepository).findByNombre("CANCELADO");
        verify(inscripcionRepository).save(inscripcion);
    }

    @Test
    void cancelarInscripcion_deberiaLanzarExcepcionSiNoExisteInscripcion() {
        // Arrange
        Long idInscripcion = 99L;
        when(inscripcionRepository.findById(idInscripcion)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            inscripcionService.cancelarInscripcion(idInscripcion);
        });

        verify(inscripcionRepository).findById(idInscripcion);
        verify(estadoInscripcionRepository, never()).findByNombre(any());
        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void historialFinalizadas_deberiaRetornarInscripcionesFinalizadasDelUsuario() {
        // Arrange
        Long idUsuario = 42L;
        List<Inscripcion> finalizadas = List.of(
                new Inscripcion(1L, LocalDate.now(), idUsuario, new Clase(), new EstadoInscripcion(3L, "FINALIZADO"))
        );

        when(inscripcionRepository.findByIdUserAndEstado_Nombre(idUsuario, "FINALIZADO"))
                .thenReturn(finalizadas);

        // Act
        List<Inscripcion> resultado = inscripcionService.historialFinalizadas(idUsuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("FINALIZADO", resultado.get(0).getEstado().getNombre());

        verify(inscripcionRepository).findByIdUserAndEstado_Nombre(idUsuario, "FINALIZADO");
    }

}

