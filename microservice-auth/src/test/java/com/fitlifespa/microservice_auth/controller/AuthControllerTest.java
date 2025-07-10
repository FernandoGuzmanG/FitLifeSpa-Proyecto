package com.fitlifespa.microservice_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlifespa.microservice_auth.dto.LoginRequest;
import com.fitlifespa.microservice_auth.dto.LoginResponse;
import com.fitlifespa.microservice_auth.dto.RegisterRequest;
import com.fitlifespa.microservice_auth.service.AuthService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /login OK")
    void loginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("usuario@correo.com", "123456");
        LoginResponse response = new LoginResponse("fake-jwt-token");

        when(authService.login(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"fake-jwt-token\"}"));
    }

    @Test
    @DisplayName("POST /login usuario no encontrado -> 401")
    void loginUsuarioNoEncontrado() throws Exception {
        LoginRequest request = new LoginRequest("usuario@correo.com", "123456");

        doThrow(new UsernameNotFoundException("Usuario no encontrado"))
                .when(authService).login(Mockito.any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Error: Usuario no encontrado"));
    }

    @Test
    @DisplayName("POST /login credenciales inválidas -> 401")
    void loginCredencialesInvalidas() throws Exception {
        LoginRequest request = new LoginRequest("usuario@correo.com", "claveMala");

        doThrow(new BadCredentialsException("Credenciales inválidas"))
                .when(authService).login(Mockito.any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Error: Credenciales inválidas"));
    }

    @Test
    @DisplayName("POST /login usuario inactivo -> 403")
    void loginUsuarioInactivo() throws Exception {
        LoginRequest request = new LoginRequest("usuario@correo.com", "123456");

        doThrow(new DisabledException("Usuario inactivo"))
                .when(authService).login(Mockito.any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Error: Usuario inactivo"));
    }

    @Test
    @DisplayName("POST /register OK")
    void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "12345678-9",
                "cliente@correo.com",
                "claveSegura123",
                "Juan",
                "Carlos",
                "González",
                "Pérez"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("¡Registro exitóso!"));
    }

    @Test
    @DisplayName("POST /register correo ya registrado -> 400")
    void registerCorreoYaRegistrado() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "12345678-9",
                "cliente@correo.com",
                "claveSegura123",
                "Juan",
                "Carlos",
                "González",
                "Pérez"
        );

        doThrow(new IllegalArgumentException("Correo ya registrado"))
                .when(authService).register(Mockito.any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Correo ya registrado"));
    }
    
    @Test
    @DisplayName("POST /register rut ya registrado -> 400")
    void registerRutYaRegistrado() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "12345678-9",
                "cliente@correo.com",
                "claveSegura123",
                "Juan",
                "Carlos",
                "González",
                "Pérez"
        );

        doThrow(new IllegalArgumentException("Rut ya registrado"))
                .when(authService).register(Mockito.any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Rut ya registrado"));
    }

    @Test
    @DisplayName("POST /register clave inválida -> 400")
    void registerClaveInvalida() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "12345678-9",
                "cliente@correo.com",
                "abc",
                "Juan",
                "Carlos",
                "González",
                "Pérez"
        );

        doThrow(new BadCredentialsException("Clave demasiado corta"))
                .when(authService).register(Mockito.any());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Clave demasiado corta"));
    }

}

