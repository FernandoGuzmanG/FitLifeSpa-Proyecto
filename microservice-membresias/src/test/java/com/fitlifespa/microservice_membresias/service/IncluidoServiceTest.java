package com.fitlifespa.microservice_membresias.service;

import com.fitlifespa.microservice_membresias.model.Incluido;
import com.fitlifespa.microservice_membresias.repository.IncluidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;


@ExtendWith(MockitoExtension.class)
class IncluidoServiceTest {

    @Mock
    private IncluidoRepository incluidoRepository;

    @InjectMocks
    private IncluidoService incluidoService;

    @Test
    void testListarPorPlanDebeRetornarListaDeIncluidos() {
        // Arrange
        Long idPlan = 1L;
        Incluido incluido1 = new Incluido();
        incluido1.setIdIncluido(1L);
        incluido1.setPlan(null);  // O asignar un plan si es necesario

        Incluido incluido2 = new Incluido();
        incluido2.setIdIncluido(2L);

        List<Incluido> incluidosMock = List.of(incluido1, incluido2);

        when(incluidoRepository.findByPlanIdPlan(idPlan)).thenReturn(incluidosMock);

        // Act
        List<Incluido> resultado = incluidoService.listarPorPlan(idPlan);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getIdIncluido());
        assertEquals(2L, resultado.get(1).getIdIncluido());

        verify(incluidoRepository, times(1)).findByPlanIdPlan(idPlan);
    }

    @Test
    void testListarPorPlanDebeRetornarListaVacia() {
        Long idPlan = 999L;

        when(incluidoRepository.findByPlanIdPlan(idPlan)).thenReturn(List.of());

        List<Incluido> resultado = incluidoService.listarPorPlan(idPlan);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(incluidoRepository, times(1)).findByPlanIdPlan(idPlan);
    }

    @Test
    void testSaveDebePersistirIncluidoConIdNulo() {
        // Arrange
        Incluido input = new Incluido();
        input.setIdIncluido(999L); // Aunque venga con ID, debe ser seteado a null internamente

        Incluido incluidoGuardado = new Incluido();
        incluidoGuardado.setIdIncluido(1L); // simula el ID asignado por la BD

        when(incluidoRepository.save(any(Incluido.class))).thenReturn(incluidoGuardado);

        // Act
        Incluido resultado = incluidoService.save(input);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdIncluido());
        verify(incluidoRepository, times(1)).save(argThat(incluido -> incluido.getIdIncluido() == null));
    }

    @Test
    void testDeleteByIdDebeLlamarAlRepositorioConElIdCorrecto() {
        // Arrange
        Long id = 5L;

        doNothing().when(incluidoRepository).deleteById(id);

        // Act
        incluidoService.deleteById(id);

        // Assert
        verify(incluidoRepository, times(1)).deleteById(id);
    }

}


