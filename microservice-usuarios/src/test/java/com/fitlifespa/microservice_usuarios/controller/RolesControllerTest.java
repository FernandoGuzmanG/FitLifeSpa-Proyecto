package com.fitlifespa.microservice_usuarios.controller;

import com.fitlifespa.microservice_usuarios.model.Enum.NombreRol;
import com.fitlifespa.microservice_usuarios.model.Rol;
import com.fitlifespa.microservice_usuarios.security.RoleValidator;
import com.fitlifespa.microservice_usuarios.service.RolService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RolesController.class)
class RolesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @MockBean
    private RoleValidator roleValidator;

    @Test
    void testMostrarRolesDebeRetornarOk() throws Exception {
        // Arrange
        Rol rol1 = new Rol(1L, NombreRol.CLIENTE, List.of());
        Rol rol2 = new Rol(2L, NombreRol.ADMINISTRADOR, List.of());
        List<Rol> listaRoles = List.of(rol1, rol2);

        when(roleValidator.getUserId(any())).thenReturn(1L); // mock de seguridad si aplica
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));
        when(rolService.findAll()).thenReturn(listaRoles);

        // Act & Assert
        mockMvc.perform(get("/api/roles")
                        .header("X-User-Roles", "ADMINISTRADOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombreRol").value("CLIENTE"))
                .andExpect(jsonPath("$[1].nombreRol").value("ADMINISTRADOR"));
    }

    @Test
    void testMostrarRolesDebeRetornarNoContentSiListaVacia() throws Exception {
        when(roleValidator.getUserId(any())).thenReturn(1L);
        doNothing().when(roleValidator).requireRole(any(), eq("ADMINISTRADOR"));
        when(rolService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/roles")
                        .header("X-User-Roles", "ADMINISTRADOR"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testMostrarRolDebeRetornarOk() throws Exception {
        Rol mockRol = new Rol();
        mockRol.setIdRol(2L);
        mockRol.setNombreRol(NombreRol.CLIENTE);

        Mockito.when(rolService.findById(2L)).thenReturn(mockRol);

        mockMvc.perform(get("/api/roles/id/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRol").value(2))
                .andExpect(jsonPath("$.nombreRol").value("CLIENTE"));
    }

    @Test
    void testMostrarRolDebeRetornarNotFound() throws Exception {
        Mockito.when(rolService.findById(anyLong())).thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(get("/api/roles/id/99"))
                .andExpect(status().isNotFound());
    }
}

