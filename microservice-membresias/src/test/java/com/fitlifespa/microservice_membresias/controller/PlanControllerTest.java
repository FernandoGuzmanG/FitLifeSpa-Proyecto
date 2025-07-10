package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.service.PlanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;

import java.util.Optional;


import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleValidator roleValidator;

    @MockBean
    private PlanService planService;

    @Test
    void testListarPlanesDebeRetornarListaDePlanes() throws Exception {
        // Arrange
        Plan plan1 = new Plan();
        plan1.setIdPlan(1L);
        plan1.setNombre("Básico");
        plan1.setDescripcion("Desc 1");
        plan1.setCosto(20.0);
        plan1.setDuracion(1);

        Plan plan2 = new Plan();
        plan2.setIdPlan(2L);
        plan2.setNombre("Premium");
        plan2.setDescripcion("Desc 2");
        plan2.setCosto(50.0);
        plan2.setDuracion(3);

        List<Plan> planes = List.of(plan1, plan2);


        when(planService.findAll()).thenReturn(planes);

        // Act & Assert
        mockMvc.perform(get("/api/planes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Básico"))
                .andExpect(jsonPath("$[1].nombre").value("Premium"));
    }

    @Test
    void testListarPlanesDebeRetornarNoContentSiListaVacia() throws Exception {
        // Arrange
        when(planService.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/planes"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerPlanDebeRetornarPlanSiExiste() throws Exception {
        // Arrange
        Long planId = 1L;
        Plan plan = new Plan();
        plan.setIdPlan(planId);
        plan.setNombre("Básico");
        plan.setDescripcion("Desc");
        plan.setCosto(19.99);
        plan.setDuracion(1);

        when(planService.findById(planId)).thenReturn(Optional.of(plan));
        doNothing().when(roleValidator).requireRole(any(), any());

        // Act & Assert
        mockMvc.perform(get("/api/planes/{id}", planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlan").value(planId))
                .andExpect(jsonPath("$.nombre").value("Básico"))
                .andExpect(jsonPath("$.descripcion").value("Desc"))
                .andExpect(jsonPath("$.costo").value(19.99))
                .andExpect(jsonPath("$.duracion").value(1));
    }

    @Test
    void testObtenerPlanDebeRetornarNotFoundSiNoExiste() throws Exception {
        // Arrange
        Long planId = 999L;
        when(planService.findById(planId)).thenReturn(Optional.empty());
        doNothing().when(roleValidator).requireRole(any(), any());

        // Act & Assert
        mockMvc.perform(get("/api/planes/{id}", planId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearPlanDebeRetornarCreatedSiExitoso() throws Exception {
        // Arrange
        Plan plan = new Plan();
        plan.setNombre("Pro");
        plan.setDescripcion("Plan profesional");
        plan.setCosto(49.99);
        plan.setDuracion(6);

        Plan creado = new Plan();
        creado.setIdPlan(1L);
        creado.setNombre("Pro");
        creado.setDescripcion("Plan profesional");
        creado.setCosto(49.99);
        creado.setDuracion(6);

        when(planService.crearPlan(any(Plan.class))).thenReturn(creado);
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        // Act & Assert
        mockMvc.perform(post("/api/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nombre": "Pro",
                          "descripcion": "Plan profesional",
                          "costo": 49.99,
                          "duracion": 6
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPlan").value(1L))
                .andExpect(jsonPath("$.nombre").value("Pro"))
                .andExpect(jsonPath("$.descripcion").value("Plan profesional"))
                .andExpect(jsonPath("$.costo").value(49.99))
                .andExpect(jsonPath("$.duracion").value(6));
    }

    @Test
    void testCrearPlanDebeRetornarNotFoundSiFalla() throws Exception {
        // Arrange
        when(planService.crearPlan(any(Plan.class))).thenThrow(new RuntimeException("error"));
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        // Act & Assert
        mockMvc.perform(post("/api/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nombre": "Pro",
                          "descripcion": "Plan profesional",
                          "costo": 49.99,
                          "duracion": 6
                        }
                    """))
                .andExpect(status().isNotFound());
    }
    @Test
    void testActualizarPlanDebeRetornarOkSiExitoso() throws Exception {
        // Arrange
        Long idPlan = 1L;
        Plan actualizado = new Plan();
        actualizado.setIdPlan(idPlan);
        actualizado.setNombre("Actualizado");
        actualizado.setDescripcion("Nuevo desc");
        actualizado.setCosto(59.99);
        actualizado.setDuracion(3);

        when(planService.actualizarPlan(eq(idPlan), any(Plan.class))).thenReturn(actualizado);
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        // Act & Assert
        mockMvc.perform(put("/api/planes/{id}", idPlan)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nombre": "Actualizado",
                          "descripcion": "Nuevo desc",
                          "costo": 59.99,
                          "duracion": 3
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlan").value(1L))
                .andExpect(jsonPath("$.nombre").value("Actualizado"))
                .andExpect(jsonPath("$.descripcion").value("Nuevo desc"))
                .andExpect(jsonPath("$.costo").value(59.99))
                .andExpect(jsonPath("$.duracion").value(3));
    }
    @Test
    void testActualizarPlanDebeRetornarNotFoundSiNoExiste() throws Exception {
        // Arrange
        Long idInexistente = 999L;
        when(planService.actualizarPlan(eq(idInexistente), any(Plan.class)))
                .thenThrow(new RuntimeException("Plan no encontrado"));

        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        // Act & Assert
        mockMvc.perform(put("/api/planes/{id}", idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nombre": "Algo",
                          "descripcion": "Desc",
                          "costo": 30.0,
                          "duracion": 2
                        }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Plan no encontrado"));
    }
    @Test
    void testActualizarPlanDebeRetornarErrorInternoSiExcepcionGenerica() throws Exception {
        Long id = 5L;
        when(planService.actualizarPlan(eq(id), any(Plan.class)))
                .thenThrow(new RuntimeException("Error inesperado"));

        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        mockMvc.perform(put("/api/planes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nombre": "Otro",
                          "descripcion": "Desc",
                          "costo": 99.0,
                          "duracion": 1
                        }
                    """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error interno al actualizar el plan"));
    }


}

