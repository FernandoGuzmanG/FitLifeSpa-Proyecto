package com.fitlifespa.microservice_clases.service;

import com.fitlifespa.microservice_clases.model.Clase;
import com.fitlifespa.microservice_clases.model.EstadoClase;
import com.fitlifespa.microservice_clases.repository.ClaseRepository;
import com.fitlifespa.microservice_clases.repository.EstadoClaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClaseServiceTest {

    @InjectMocks
    private ClaseService claseService;

    @Mock
    private ClaseRepository claseRepository;

    @Mock
    private EstadoClaseRepository estadoClaseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearClase_Success() {
        String nombre = "Yoga Avanzado";
        String descripcion = "Clase avanzada";
        Long idServicio = 2L;
        Long idEntrenador = 5L;
        LocalDate fecha = LocalDate.now();

        EstadoClase estado = new EstadoClase(1L, "ACTIVO");
        when(estadoClaseRepository.findByNombre("ACTIVO")).thenReturn(Optional.of(estado));

        Clase claseGuardada = new Clase();
        when(claseRepository.save(any(Clase.class))).thenReturn(claseGuardada);

        Clase resultado = claseService.crearClase(nombre, descripcion, idServicio, idEntrenador, fecha);

        assertNotNull(resultado);
        verify(estadoClaseRepository).findByNombre("ACTIVO");
        verify(claseRepository).save(any(Clase.class));
    }

    @Test
    void crearClase_EstadoNoEncontrado() {
        when(estadoClaseRepository.findByNombre("ACTIVO")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> 
            claseService.crearClase("Yoga", "Avanzado", 1L, 1L, LocalDate.now()));

        verify(estadoClaseRepository).findByNombre("ACTIVO");
        verify(claseRepository, never()).save(any(Clase.class));
    }

    @Test
    void actualizarEntrenador_Success() {
        Long idClase = 1L;
        Long idUsuario = 10L;

        Clase clase = new Clase();
        when(claseRepository.findById(idClase)).thenReturn(Optional.of(clase));

        Clase resultado = claseService.actualizarEntrenador(idClase, idUsuario);

        assertEquals(idUsuario, resultado.getIdUsuario());
        verify(claseRepository).findById(idClase);
    }

    @Test
    void actualizarEntrenador_ClaseNoEncontrada() {
        when(claseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> claseService.actualizarEntrenador(1L, 2L));
        verify(claseRepository).findById(1L);
    }

    @Test
    void cambiarEstado_Success() {
        Long idClase = 1L;
        String nuevoEstado = "CANCELADA";

        Clase clase = new Clase();
        when(claseRepository.findById(idClase)).thenReturn(Optional.of(clase));

        EstadoClase estado = new EstadoClase(2L, "CANCELADA");
        when(estadoClaseRepository.findByNombre(nuevoEstado)).thenReturn(Optional.of(estado));

        when(claseRepository.save(any(Clase.class))).thenReturn(clase);

        Clase resultado = claseService.cambiarEstado(idClase, nuevoEstado);

        assertEquals(estado, resultado.getEstado());
        verify(claseRepository).findById(idClase);
        verify(estadoClaseRepository).findByNombre(nuevoEstado);
        verify(claseRepository).save(clase);
    }

    @Test
    void cambiarEstado_ClaseNoEncontrada() {
        when(claseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> claseService.cambiarEstado(1L, "PROGRAMADA"));
        verify(claseRepository).findById(1L);
    }

    @Test
    void cambiarEstado_EstadoNoEncontrado() {
        Clase clase = new Clase();
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));
        when(estadoClaseRepository.findByNombre("INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> claseService.cambiarEstado(1L, "INEXISTENTE"));

        verify(claseRepository).findById(1L);
        verify(estadoClaseRepository).findByNombre("INEXISTENTE");
    }

    @Test
    void buscarPorId_Success() {
        Clase clase = new Clase();
        when(claseRepository.findById(1L)).thenReturn(Optional.of(clase));

        Optional<Clase> resultado = claseService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        verify(claseRepository).findById(1L);
    }

    @Test
    void listarPorEntrenador_Success() {
        Long idEntrenador = 5L;
        when(claseRepository.findByIdUsuario(idEntrenador)).thenReturn(List.of(new Clase(), new Clase()));

        List<Clase> clases = claseService.listarPorEntrenador(idEntrenador);

        assertEquals(2, clases.size());
        verify(claseRepository).findByIdUsuario(idEntrenador);
    }
}

