package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.model.Incluido;
import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.service.IncluidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.List;


@WebMvcTest(IncluidoController.class)
class IncluidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncluidoService incluidoService;

    @MockBean
    private RoleValidator roleValidator;

    @Test
    void testListarPorPlanDebeRetornarListaConLinks() throws Exception {
        Long idPlan = 1L;

        Incluido incluido = new Incluido();
        incluido.setIdIncluido(1L);
        incluido.setPlan(new Plan());

        when(incluidoService.listarPorPlan(idPlan)).thenReturn(List.of(incluido));

        mockMvc.perform(get("/api/incluidos/plan/{idPlan}", idPlan))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.incluidoList[0].idIncluido").value(1))
                .andExpect(jsonPath("_embedded.incluidoList[0]._links.eliminar.href").exists())
                .andExpect(jsonPath("_links.crear.href").exists());
    }

    @Test
    void testListarPorPlanDebeRetornarNoContentSiListaVacia() throws Exception {
        when(incluidoService.listarPorPlan(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/incluidos/plan/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCrearIncluidoDebeRetornarCreatedConLinks() throws Exception {
        Incluido requestIncluido = new Incluido();
        Plan plan = new Plan();
        plan.setIdPlan(1L);
        requestIncluido.setPlan(plan);

        Incluido incluidoGuardado = new Incluido();
        incluidoGuardado.setIdIncluido(10L);
        incluidoGuardado.setPlan(plan);

        when(incluidoService.save(any(Incluido.class))).thenReturn(incluidoGuardado);
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        mockMvc.perform(post("/api/incluidos")
                        .contentType("application/json")
                        .content("""
                        {
                          "plan": {
                            "idPlan": 1
                          }
                        }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("idIncluido").value(10))
                .andExpect(jsonPath("_links.listar-por-plan.href").exists())
                .andExpect(jsonPath("_links.eliminar.href").exists());
    }

    @Test
    void testCrearIncluidoDebeRetornarErrorSiFalla() throws Exception {
        doThrow(new RuntimeException("fallo")).when(incluidoService).save(any(Incluido.class));
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        mockMvc.perform(post("/api/incluidos")
                        .contentType("application/json")
                        .content("""
                        {
                          "plan": {
                            "idPlan": 1
                          }
                        }
                    """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("Error al crear el servicio incluido"));
    }

    @Test
    void testEliminarIncluidoDebeRetornarNoContent() throws Exception {
        Long idIncluido = 1L;

        doNothing().when(incluidoService).deleteById(idIncluido);
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        mockMvc.perform(delete("/api/incluidos/{id}", idIncluido))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarIncluidoDebeRetornarNotFoundSiFalla() throws Exception {
        Long idIncluido = 99L;

        doThrow(new RuntimeException("no encontrado")).when(incluidoService).deleteById(idIncluido);
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));

        mockMvc.perform(delete("/api/incluidos/{id}", idIncluido))
                .andExpect(status().isNotFound());
    }
}
