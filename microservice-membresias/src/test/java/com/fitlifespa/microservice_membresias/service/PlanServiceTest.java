package com.fitlifespa.microservice_membresias.service;

import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.repository.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    @Test
    void testFindAllDebeRetornarListaDePlanes() {
        // Arrange
        Plan plan1 = new Plan();
        plan1.setIdPlan(1L);
        plan1.setNombre("Básico");

        Plan plan2 = new Plan();
        plan2.setIdPlan(2L);
        plan2.setNombre("Premium");

        List<Plan> mockPlanes = List.of(plan1, plan2);
        when(planRepository.findAll()).thenReturn(mockPlanes);

        // Act
        List<Plan> result = planService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Básico", result.get(0).getNombre());
        verify(planRepository, times(1)).findAll();
    }

    @Test
    void testFindAllDebeRetornarListaVaciaSiNoHayPlanes() {
        // Arrange
        when(planRepository.findAll()).thenReturn(List.of());

        // Act
        List<Plan> result = planService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdDebeRetornarPlanSiExiste() {
        // Arrange
        Long idPlan = 1L;
        Plan plan = new Plan();
        plan.setIdPlan(idPlan);
        plan.setNombre("Pro");

        when(planRepository.findById(idPlan)).thenReturn(Optional.of(plan));

        // Act
        Optional<Plan> resultado = planService.findById(idPlan);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Pro", resultado.get().getNombre());
        verify(planRepository, times(1)).findById(idPlan);
    }

    @Test
    void testFindByIdDebeRetornarVacioSiNoExiste() {
        // Arrange
        Long idInexistente = 99L;
        when(planRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act
        Optional<Plan> resultado = planService.findById(idInexistente);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(planRepository, times(1)).findById(idInexistente);
    }

    @Test
    void testActualizarPlanDebeActualizarYRetornarPlan() {
        // Arrange
        Long idPlan = 1L;

        Plan existente = new Plan();
        existente.setIdPlan(idPlan);
        existente.setNombre("Básico");
        existente.setDescripcion("Acceso limitado");
        existente.setCosto(19.99);
        existente.setDuracion(1);

        Plan actualizado = new Plan();
        actualizado.setNombre("Premium");
        actualizado.setDescripcion("Acceso total");
        actualizado.setCosto(49.99);
        actualizado.setDuracion(3);

        when(planRepository.findById(idPlan)).thenReturn(Optional.of(existente));
        when(planRepository.save(any(Plan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Plan resultado = planService.actualizarPlan(idPlan, actualizado);

        // Assert
        assertEquals("Premium", resultado.getNombre());
        assertEquals("Acceso total", resultado.getDescripcion());
        assertEquals(49.99, resultado.getCosto());
        assertEquals(3, resultado.getDuracion());

        verify(planRepository, times(1)).findById(idPlan);
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    @Test
    void testActualizarPlanDebeLanzarExcepcionSiNoExiste() {
        // Arrange
        Long idInexistente = 99L;
        Plan datos = new Plan();
        when(planRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            planService.actualizarPlan(idInexistente, datos);
        });

        assertEquals("Plan no encontrado", ex.getMessage());
        verify(planRepository, times(1)).findById(idInexistente);
        verify(planRepository, never()).save(any());
    }

    @Test
    void testCrearPlanDebeGuardarYRetornarPlan() {
        // Arrange
        Plan nuevoPlan = new Plan();
        nuevoPlan.setNombre("Fit Básico");
        nuevoPlan.setDescripcion("Acceso a zonas generales");
        nuevoPlan.setCosto(25.0);
        nuevoPlan.setDuracion(1);

        Plan planGuardado = new Plan();
        planGuardado.setIdPlan(1L);
        planGuardado.setNombre("Fit Básico");
        planGuardado.setDescripcion("Acceso a zonas generales");
        planGuardado.setCosto(25.0);
        planGuardado.setDuracion(1);

        when(planRepository.save(any(Plan.class))).thenReturn(planGuardado);

        // Act
        Plan resultado = planService.crearPlan(nuevoPlan);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPlan());
        assertEquals("Fit Básico", resultado.getNombre());
        assertEquals("Acceso a zonas generales", resultado.getDescripcion());

        verify(planRepository, times(1)).save(any(Plan.class));
    }

}
