package com.fitlifespa.microservice_membresias.service;

import com.fitlifespa.microservice_membresias.model.Membresia;
import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.repository.MembresiaRepository;
import com.fitlifespa.microservice_membresias.repository.PlanRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembresiaServiceTest {

    @Mock
    private MembresiaRepository membresiaRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private MembresiaService membresiaService;

    @Test
    void testFindAllDebeRetornarListaDeMembresias() {
        // Arrange
        List<Membresia> mockMembresias = List.of(
                new Membresia(1L, LocalDate.now(), LocalDate.now().plusDays(30), 101L, 49.99, new Plan()),
                new Membresia(2L, LocalDate.now(), LocalDate.now().plusDays(30), 102L, 59.99, new Plan())
        );
        when(membresiaRepository.findAll()).thenReturn(mockMembresias);

        // Act
        List<Membresia> resultado = membresiaService.findAll();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(101L, resultado.get(0).getIdUsuario());
        assertEquals(59.99, resultado.get(1).getCostoTotal());
        verify(membresiaRepository).findAll();
    }

    @Test
    void testCrearMembresiaDebeGuardarYRetornarLaMembresia() {
        // Arrange
        Long idCliente = 200L;
        Plan plan = new Plan(1L, "Premium", "Acceso total", 49.99, 1, null);

        Membresia membresiaInput = new Membresia();
        membresiaInput.setPlan(plan);
        membresiaInput.setCostoTotal(49.99);

        // Simulamos que el repositorio devuelve la misma membresía que se le pasa
        when(membresiaRepository.save(any(Membresia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Membresia resultado = membresiaService.crearMembresia(membresiaInput, idCliente);

        // Assert
        Assertions.assertNotNull(resultado);
        assertEquals(idCliente, resultado.getIdUsuario());
        Assertions.assertNull(resultado.getIdMembresia());
        assertEquals(LocalDate.now(), resultado.getFechaInicio());
        assertEquals(LocalDate.now().plusDays(30), resultado.getFechaTermino());

        verify(membresiaRepository).save(resultado);
    }

    @Test
    void testRenovarMembresiaDebeCrearNuevaMembresia() {
        // Arrange
        Long idUsuario = 1L;
        Long idMembresia = 100L;

        Plan plan = new Plan(1L, "Mensual", "Plan básico", 39.99, 1, null);
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaTermino = fechaInicio.plusMonths(1);

        Membresia membresiaActual = new Membresia(
                idMembresia, fechaInicio, fechaTermino, idUsuario, 39.99, plan
        );

        when(membresiaRepository.findById(idMembresia)).thenReturn(Optional.of(membresiaActual));
        when(membresiaRepository.save(any(Membresia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Membresia nueva = membresiaService.renovarMembresia(idMembresia, idUsuario);

        // Assert
        Assertions.assertNotNull(nueva);
        assertEquals(idUsuario, nueva.getIdUsuario());
        assertEquals(plan, nueva.getPlan());
        assertEquals(plan.getCosto(), nueva.getCostoTotal());
        assertEquals(fechaTermino.plusDays(plan.getDuracion()), nueva.getFechaInicio());
        assertEquals(fechaTermino.plusDays(plan.getDuracion()).plusMonths(plan.getDuracion()), nueva.getFechaTermino());

        verify(membresiaRepository).findById(idMembresia);
        verify(membresiaRepository).save(any(Membresia.class));
    }

    @Test
    void testRenovarMembresiaDebeLanzarExcepcionSiNoPerteneceAlUsuario() {
        Long idUsuario = 1L;
        Long idMembresia = 100L;

        Membresia membresia = new Membresia();
        membresia.setIdUsuario(999L); // otro usuario

        when(membresiaRepository.findById(idMembresia)).thenReturn(Optional.of(membresia));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                membresiaService.renovarMembresia(idMembresia, idUsuario)
        );

        assertEquals("La membresía no pertenece al usuario", exception.getMessage());
        verify(membresiaRepository).findById(idMembresia);
        verify(membresiaRepository, never()).save(any());
    }

    @Test
    void testRenovarMembresiaDebeLanzarExcepcionSiNoExiste() {
        Long idMembresia = 100L;
        Long idUsuario = 1L;

        when(membresiaRepository.findById(idMembresia)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                membresiaService.renovarMembresia(idMembresia, idUsuario)
        );

        assertEquals("Membresía no encontrada", exception.getMessage());
        verify(membresiaRepository).findById(idMembresia);
        verify(membresiaRepository, never()).save(any());
    }

    @Test
    void testCancelarMembresiaDebeEliminarSiEsValida() {
        Long idMembresia = 1L;
        Long idUsuario = 10L;
        LocalDate fechaInicio = LocalDate.now().minusDays(3);

        Membresia membresia = new Membresia();
        membresia.setIdMembresia(idMembresia);
        membresia.setIdUsuario(idUsuario);
        membresia.setFechaInicio(fechaInicio);

        when(membresiaRepository.findById(idMembresia)).thenReturn(Optional.of(membresia));

        // Act
        membresiaService.cancelarMembresia(idMembresia, idUsuario);

        // Assert
        verify(membresiaRepository).findById(idMembresia);
        verify(membresiaRepository).delete(membresia);
    }

    @Test
    void testCancelarMembresiaDebeFallarSiYaPasaron7Dias() {
        Long idMembresia = 1L;
        Long idUsuario = 10L;
        LocalDate fechaInicio = LocalDate.now().minusDays(8);

        Membresia membresia = new Membresia();
        membresia.setIdUsuario(idUsuario);
        membresia.setFechaInicio(fechaInicio);

        when(membresiaRepository.findById(idMembresia)).thenReturn(Optional.of(membresia));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                membresiaService.cancelarMembresia(idMembresia, idUsuario)
        );

        assertEquals("La membresía ya no puede ser cancelada", exception.getMessage());
        verify(membresiaRepository, never()).delete(any());
    }

    @Test
    void testCancelarMembresiaDebeFallarSiNoExiste() {
        Long idMembresia = 1L;
        Long idUsuario = 10L;

        when(membresiaRepository.findById(idMembresia)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                membresiaService.cancelarMembresia(idMembresia, idUsuario)
        );

        assertEquals("Membresía no encontrada", exception.getMessage());
        verify(membresiaRepository, never()).delete(any());
    }

    @Test
    void testCambiarPlanDebeActualizarPlanYGuardar() {
        Long idMembresia = 1L;
        Long idUsuario = 100L;
        Long nuevoIdPlan = 2L;

        Plan planAnterior = new Plan(1L, "Básico", "Desc", 29.99, 1, null);
        Plan planNuevo = new Plan(nuevoIdPlan, "Premium", "Full acceso", 59.99, 1, null);

        Membresia membresia = new Membresia();
        membresia.setIdMembresia(idMembresia);
        membresia.setIdUsuario(idUsuario);
        membresia.setFechaTermino(LocalDate.now().plusDays(5));
        membresia.setPlan(planAnterior);

        when(membresiaRepository.findById(idMembresia)).thenReturn(Optional.of(membresia));
        when(planRepository.findById(nuevoIdPlan)).thenReturn(Optional.of(planNuevo));
        when(membresiaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Membresia actualizada = membresiaService.cambiarPlanMembresia(idMembresia, idUsuario, nuevoIdPlan);

        // Assert
        assertEquals(planNuevo, actualizada.getPlan());
        assertEquals(59.99, actualizada.getCostoTotal());
        verify(membresiaRepository).save(membresia);
    }


    @Test
    void testCambiarPlanDebeFallarSiNoPerteneceAlUsuario() {
        Membresia membresia = new Membresia();
        membresia.setIdUsuario(999L);

        when(membresiaRepository.findById(1L)).thenReturn(Optional.of(membresia));

        RuntimeException e = assertThrows(RuntimeException.class, () ->
                membresiaService.cambiarPlanMembresia(1L, 100L, 2L));

        assertEquals("La membresía no pertenece al usuario", e.getMessage());
        verify(membresiaRepository, never()).save(any());
    }

    @Test
    void testCambiarPlanDebeFallarSiMembresiaExpirada() {
        Membresia membresia = new Membresia();
        membresia.setIdUsuario(100L);
        membresia.setFechaTermino(LocalDate.now().minusDays(1));

        when(membresiaRepository.findById(1L)).thenReturn(Optional.of(membresia));

        RuntimeException e = assertThrows(RuntimeException.class, () ->
                membresiaService.cambiarPlanMembresia(1L, 100L, 2L));

        assertEquals("No se puede cambiar el plan de una membresía expirada", e.getMessage());
        verify(membresiaRepository, never()).save(any());
    }




}

