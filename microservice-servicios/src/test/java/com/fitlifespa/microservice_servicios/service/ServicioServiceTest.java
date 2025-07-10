package com.fitlifespa.microservice_servicios.service;

import com.fitlifespa.microservice_servicios.model.EstadoServicio;
import com.fitlifespa.microservice_servicios.model.Servicio;
import com.fitlifespa.microservice_servicios.repository.EstadoServicioRepository;
import com.fitlifespa.microservice_servicios.repository.ServicioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepo;

    @Mock
    private EstadoServicioRepository estadoRepo;

    @InjectMocks
    private ServicioService servicioService;

    @Test
    void testCrearServicioConEstadoActivo() {
        EstadoServicio estadoActivo = new EstadoServicio(1L, "ACTIVO");
        String nombre = "Masaje";
        String descripcion = "Masaje relajante";

        when(estadoRepo.findByNombre("ACTIVO")).thenReturn(Optional.of(estadoActivo));

        Servicio servicioEsperado = new Servicio();
        servicioEsperado.setNombre(nombre);
        servicioEsperado.setDescripcion(descripcion);
        servicioEsperado.setEstado(estadoActivo);

        when(servicioRepo.save(any(Servicio.class))).thenAnswer(invoc -> invoc.getArgument(0));

        Servicio resultado = servicioService.crear(nombre, descripcion);

        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombre());
        assertEquals(descripcion, resultado.getDescripcion());
        assertEquals("ACTIVO", resultado.getEstado().getNombre());

        verify(estadoRepo, times(1)).findByNombre("ACTIVO");
        verify(servicioRepo, times(1)).save(any(Servicio.class));
    }

    @Test
    void testListarServiciosActivos() {
        EstadoServicio activo = new EstadoServicio(1L, "ACTIVO");
        EstadoServicio inactivo = new EstadoServicio(2L, "INACTIVO");

        List<Servicio> mockServicios = List.of(
                new Servicio(1L, "Masaje", "Relajante", activo),
                new Servicio(2L, "Yoga", "Clase grupal", inactivo),
                new Servicio(3L, "Spa", "Con aceites esenciales", activo)
        );

        when(servicioRepo.findAll()).thenReturn(mockServicios);

        List<Servicio> resultado = servicioService.listarActivos();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(s -> "ACTIVO".equalsIgnoreCase(s.getEstado().getNombre())));

        verify(servicioRepo, times(1)).findAll();
    }
    @Test
    void testActualizarServicioExistente() {
        Long id = 1L;
        String nuevoNombre = "Pilates";
        String nuevaDescripcion = "Ejercicio suave";

        EstadoServicio estado = new EstadoServicio(1L, "ACTIVO");
        Servicio servicioExistente = new Servicio(id, "Yoga", "Clase grupal", estado);

        when(servicioRepo.findById(id)).thenReturn(Optional.of(servicioExistente));
        when(servicioRepo.save(any(Servicio.class))).thenAnswer(invoc -> invoc.getArgument(0));

        Servicio actualizado = servicioService.actualizar(id, nuevoNombre, nuevaDescripcion);

        assertNotNull(actualizado);
        assertEquals(nuevoNombre, actualizado.getNombre());
        assertEquals(nuevaDescripcion, actualizado.getDescripcion());
        assertEquals(id, actualizado.getId());
        verify(servicioRepo).findById(id);
        verify(servicioRepo).save(servicioExistente);
    }

    @Test
    void testActualizarServicioNoExistenteDebeLanzarExcepcion() {
        Long idInexistente = 999L;

        when(servicioRepo.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            servicioService.actualizar(idInexistente, "Nuevo", "Desc");
        });

        assertEquals("Servicio no encontrado", ex.getMessage());
        verify(servicioRepo).findById(idInexistente);
        verify(servicioRepo, never()).save(any());
    }

    @Test
    void testDesactivarServicioExistente() {
        Long id = 1L;
        EstadoServicio estadoActivo = new EstadoServicio(1L, "ACTIVO");
        EstadoServicio estadoInactivo = new EstadoServicio(2L, "INACTIVO");

        Servicio servicio = new Servicio(id, "Masaje", "Relajante", estadoActivo);

        when(servicioRepo.findById(id)).thenReturn(Optional.of(servicio));
        when(estadoRepo.findByNombre("INACTIVO")).thenReturn(Optional.of(estadoInactivo));
        when(servicioRepo.save(any(Servicio.class))).thenAnswer(invoc -> invoc.getArgument(0));

        Servicio resultado = servicioService.desactivarServicio(id);

        assertNotNull(resultado);
        assertEquals("INACTIVO", resultado.getEstado().getNombre());

        verify(servicioRepo).findById(id);
        verify(estadoRepo).findByNombre("INACTIVO");
        verify(servicioRepo).save(servicio);
    }

    @Test
    void testDesactivarServicioNoExistenteDebeLanzarExcepcion() {
        Long idInexistente = 999L;
        when(servicioRepo.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            servicioService.desactivarServicio(idInexistente);
        });

        assertEquals("Servicio no encontrado", ex.getMessage());
        verify(servicioRepo).findById(idInexistente);
        verify(estadoRepo, never()).findByNombre("INACTIVO");
        verify(servicioRepo, never()).save(any());
    }

    @Test
    void testDesactivarServicioEstadoInvalidoDebeLanzarExcepcion() {
        Long id = 1L;
        Servicio servicio = new Servicio(id, "Spa", "Aceites esenciales", new EstadoServicio(1L, "ACTIVO"));

        when(servicioRepo.findById(id)).thenReturn(Optional.of(servicio));
        when(estadoRepo.findByNombre("INACTIVO")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            servicioService.desactivarServicio(id);
        });

        assertEquals("Estado no válido", ex.getMessage());
        verify(servicioRepo).findById(id);
        verify(estadoRepo).findByNombre("INACTIVO");
        verify(servicioRepo, never()).save(any());
    }
}

