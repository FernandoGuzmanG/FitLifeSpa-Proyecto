package com.fitlifespa.microservice_membresias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlifespa.microservice_membresias.model.Membresia;
import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.service.MembresiaService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

import java.time.LocalDate;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebMvcTest(MembresiaController.class)
class MembresiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MembresiaService membresiaService;

    @MockBean
    private RoleValidator roleValidator;

    @MockBean
    private PlanController planController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListarMembresiasDebeRetornarListaConLinks() throws Exception {
        Plan plan = new Plan(1L, "Premium", "Desc", 59.99, 1, null);
        Membresia membresia = new Membresia(1L, LocalDate.now(), LocalDate.now().plusDays(30), 10L, 59.99, plan);

        when(membresiaService.findAll()).thenReturn(List.of(membresia));
        doNothing().when(roleValidator).requireRole(any(HttpServletRequest.class), eq("ADMINISTRADOR"));

        mockMvc.perform(get("/api/membresias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.membresiaList[0].idMembresia").value(1))
                .andExpect(jsonPath("$._embedded.membresiaList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.membresiaList[0]._links.plan.href").exists());
    }

    @Test
    void testListarMembresiasDebeRetornarNoContentSiListaVacia() throws Exception {
        when(membresiaService.findAll()).thenReturn(List.of());

        doNothing().when(roleValidator).requireRole(any(HttpServletRequest.class), eq("ADMINISTRADOR"));

        mockMvc.perform(get("/api/membresias"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testObtenerPorIdDebeRetornarMembresiaConLinkAlPlan() throws Exception {
        // Datos de prueba
        Long idMembresia = 1L;
        Long idPlan = 2L;

        Plan plan = new Plan();
        plan.setIdPlan(idPlan);
        plan.setNombre("Gold");
        plan.setDescripcion("Acceso completo");
        plan.setCosto(99.99);
        plan.setDuracion(1);

        Membresia membresia = new Membresia();
        membresia.setIdMembresia(idMembresia);
        membresia.setFechaInicio(LocalDate.now());
        membresia.setFechaTermino(LocalDate.now().plusMonths(1));
        membresia.setIdUsuario(5L);
        membresia.setPlan(plan);
        membresia.setCostoTotal(plan.getCosto());

        when(membresiaService.findById(idMembresia)).thenReturn(Optional.of(membresia));
        doNothing().when(roleValidator).requireRole(any(HttpServletRequest.class), eq("ADMINISTRADOR"));

        mockMvc.perform(get("/api/membresias/{id}", idMembresia))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMembresia").value(idMembresia))
                .andExpect(jsonPath("$._links.plan.href").exists());
    }

    @Test
    void testRenovarMembresiaDebeRetornarNuevaMembresiaConLinkAlPlan() throws Exception {
        // Datos de prueba
        Long idMembresia = 1L;
        Long idUsuario = 100L;

        Plan plan = new Plan();
        plan.setIdPlan(5L);
        plan.setNombre("Premium");
        plan.setDescripcion("Acceso completo");
        plan.setCosto(49.99);
        plan.setDuracion(1);

        Membresia nuevaMembresia = new Membresia();
        nuevaMembresia.setIdMembresia(2L);
        nuevaMembresia.setFechaInicio(LocalDate.now());
        nuevaMembresia.setFechaTermino(LocalDate.now().plusMonths(1));
        nuevaMembresia.setIdUsuario(idUsuario);
        nuevaMembresia.setPlan(plan);
        nuevaMembresia.setCostoTotal(plan.getCosto());

        when(membresiaService.renovarMembresia(idMembresia, idUsuario)).thenReturn(nuevaMembresia);

        mockMvc.perform(put("/api/membresias/renovar")
                        .param("idMembresia", idMembresia.toString())
                        .param("idUsuario", idUsuario.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMembresia").value(2L))
                .andExpect(jsonPath("$.idUsuario").value(idUsuario))
                .andExpect(jsonPath("$._links.plan.href").exists());
    }

    @Test
    void testCrearMembresiaDebeRetornarMembresiaConLinks() throws Exception {
        // Arrange
        Long idUsuario = 100L;
        Plan plan = new Plan();
        plan.setIdPlan(1L);
        plan.setNombre("Premium");
        plan.setCosto(59.99);
        plan.setDuracion(1);

        Membresia requestMembresia = new Membresia();
        requestMembresia.setPlan(plan);

        Membresia nuevaMembresia = new Membresia();
        nuevaMembresia.setIdMembresia(10L);
        nuevaMembresia.setIdUsuario(idUsuario);
        nuevaMembresia.setPlan(plan);
        nuevaMembresia.setCostoTotal(59.99);
        nuevaMembresia.setFechaInicio(LocalDate.now());
        nuevaMembresia.setFechaTermino(LocalDate.now().plusDays(30));

        when(membresiaService.crearMembresia(any(Membresia.class), eq(idUsuario))).thenReturn(nuevaMembresia);
        doNothing().when(roleValidator).requireRole(any(), eq("CLIENTE"));

        String jsonRequest = """
        {
            "plan": {
                "idPlan": 1
            }
        }
        """;

        mockMvc.perform(post("/api/membresias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("X-User-Id", idUsuario.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMembresia").value(10))
                .andExpect(jsonPath("$.plan.idPlan").value(1))
                .andExpect(jsonPath("$._links.renovar.href").exists())
                .andExpect(jsonPath("$._links.cambiar-plan.href").exists())
                .andExpect(jsonPath("$._links.cancelar.href").exists());
    }

    @Test
    void testRenovarMembresiaDebeRetornarNuevaMembresiaConLinks() throws Exception {
        Long idMembresia = 10L;
        Long idUsuario = 100L;

        Plan plan = new Plan();
        plan.setIdPlan(1L);
        plan.setNombre("Premium");
        plan.setDuracion(1);
        plan.setCosto(59.99);

        Membresia nuevaMembresia = new Membresia();
        nuevaMembresia.setIdMembresia(20L); // nueva membresía creada tras renovar
        nuevaMembresia.setIdUsuario(idUsuario);
        nuevaMembresia.setFechaInicio(LocalDate.now());
        nuevaMembresia.setFechaTermino(LocalDate.now().plusMonths(1));
        nuevaMembresia.setPlan(plan);
        nuevaMembresia.setCostoTotal(59.99);

        when(membresiaService.renovarMembresia(idMembresia, idUsuario)).thenReturn(nuevaMembresia);
        doNothing().when(roleValidator).requireRole(any(), eq("CLIENTE"));

        mockMvc.perform(put("/api/membresias/renovar/{idMembresia}", idMembresia)
                        .header("X-User-Id", idUsuario.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMembresia").value(20))
                .andExpect(jsonPath("$.plan.idPlan").value(1))
                .andExpect(jsonPath("$._links.cambiar-plan.href").exists())
                .andExpect(jsonPath("$._links.cancelar.href").exists());
    }

    @Test
    void testCambiarPlanDebeRetornarMembresiaActualizadaConLinks() throws Exception {
        Long idMembresia = 1L;
        Long idUsuario = 10L;
        Long nuevoIdPlan = 2L;

        Plan nuevoPlan = new Plan();
        nuevoPlan.setIdPlan(nuevoIdPlan);
        nuevoPlan.setNombre("Plus");
        nuevoPlan.setDescripcion("Plan Plus");
        nuevoPlan.setCosto(79.99);
        nuevoPlan.setDuracion(2);

        Membresia actualizada = new Membresia();
        actualizada.setIdMembresia(idMembresia);
        actualizada.setIdUsuario(idUsuario);
        actualizada.setFechaInicio(LocalDate.now());
        actualizada.setFechaTermino(LocalDate.now().plusMonths(2));
        actualizada.setPlan(nuevoPlan);
        actualizada.setCostoTotal(79.99);

        when(membresiaService.cambiarPlanMembresia(idMembresia, idUsuario, nuevoIdPlan)).thenReturn(actualizada);
        doNothing().when(roleValidator).requireRole(any(), eq("CLIENTE"));

        mockMvc.perform(put("/api/membresias/cambiar-plan")
                        .param("idMembresia", idMembresia.toString())
                        .param("nuevoIdPlan", nuevoIdPlan.toString())
                        .header("X-User-Id", idUsuario.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMembresia").value(idMembresia))
                .andExpect(jsonPath("$.plan.idPlan").value(nuevoIdPlan))
                .andExpect(jsonPath("$.plan.nombre").value("Plus"))
                .andExpect(jsonPath("$._links.renovar.href").exists())
                .andExpect(jsonPath("$._links.cancelar.href").exists());
    }



}

