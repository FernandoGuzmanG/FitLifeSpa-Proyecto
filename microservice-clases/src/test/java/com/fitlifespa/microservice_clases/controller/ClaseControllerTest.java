package com.fitlifespa.microservice_clases.controller;

import com.fitlifespa.microservice_clases.model.Clase;
import com.fitlifespa.microservice_clases.service.ClaseService;
import com.fitlifespa.microservice_clases.security.RoleValidator;
import com.fitlifespa.microservice_clases.dto.CrearClaseRequest;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;

class ClaseControllerTest {

    @InjectMocks
    private ClaseController claseController;

    @Mock
    private ClaseService claseService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearClase_Success() {
        CrearClaseRequest request = new CrearClaseRequest();
        request.setNombre("Yoga");
        request.setDescripcion("Clase de Yoga");
        request.setIdServicio(1L);
        request.setIdEntrenador(2L);
        request.setFecha(LocalDate.now());

        Clase claseMock = new Clase();
        when(claseService.crearClase(any(), any(), any(), any(), any())).thenReturn(claseMock);

        // Simula validación exitosa
        doNothing().when(validator).requireRole(httpServletRequest, "COORDINADOR");

        ResponseEntity<?> response = claseController.crearClase(request, httpServletRequest);

        assertEquals(CREATED, response.getStatusCode());
        assertEquals(claseMock, response.getBody());

        verify(validator).requireRole(httpServletRequest, "COORDINADOR");
        verify(claseService).crearClase(any(), any(), any(), any(), any());
    }

    @Test
    void crearClase_BadRequest() {
        CrearClaseRequest request = new CrearClaseRequest();
        request.setNombre("Yoga");

        doNothing().when(validator).requireRole(httpServletRequest, "COORDINADOR");
        when(claseService.crearClase(any(), any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = claseController.crearClase(request, httpServletRequest);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void cambiarEntrenador_Success() {
        Clase claseMock = new Clase();
        Long idClase = 1L;
        Long nuevoEntrenador = 5L;

        doNothing().when(validator).requireRole(httpServletRequest, "COORDINADOR");
        when(claseService.actualizarEntrenador(idClase, nuevoEntrenador)).thenReturn(claseMock);

        ResponseEntity<?> response = claseController.cambiarEntrenador(idClase, nuevoEntrenador, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(claseMock, response.getBody());

        verify(validator).requireRole(httpServletRequest, "COORDINADOR");
        verify(claseService).actualizarEntrenador(idClase, nuevoEntrenador);
    }

    @Test
    void cambiarEntrenador_NotFound() {
        Long idClase = 1L;
        Long nuevoEntrenador = 5L;

        doNothing().when(validator).requireRole(httpServletRequest, "COORDINADOR");
        when(claseService.actualizarEntrenador(idClase, nuevoEntrenador))
            .thenThrow(new RuntimeException("Clase no encontrada"));

        ResponseEntity<?> response = claseController.cambiarEntrenador(idClase, nuevoEntrenador, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Clase no encontrada"));
    }

    @Test
    void cambiarEstado_Success() {
        Long idClase = 1L;
        String nuevoEstado = "CANCELADA";

        Clase claseMock = new Clase();
        doNothing().when(validator).requireRole(httpServletRequest, "COORDINADOR");
        when(claseService.cambiarEstado(idClase, nuevoEstado)).thenReturn(claseMock);

        ResponseEntity<?> response = claseController.cambiarEstado(idClase, nuevoEstado, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(claseMock, response.getBody());
    }

    @Test
    void cambiarEstado_NotFound() {
        Long idClase = 1L;
        String nuevoEstado = "INEXISTENTE";

        doNothing().when(validator).requireRole(httpServletRequest, "COORDINADOR");
        when(claseService.cambiarEstado(idClase, nuevoEstado))
            .thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<?> response = claseController.cambiarEstado(idClase, nuevoEstado, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No encontrado"));
    }

    @Test
    void clasesDelEntrenador_Success() {
        doNothing().when(validator).requireRole(httpServletRequest, "ENTRENADOR");
        when(validator.getUserId(httpServletRequest)).thenReturn(2L);

        List<Clase> listaMock = List.of(new Clase(), new Clase());
        when(claseService.listarPorEntrenador(2L)).thenReturn(listaMock);

        ResponseEntity<List<Clase>> response = claseController.clasesDelEntrenador(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(validator).requireRole(httpServletRequest, "ENTRENADOR");
        verify(claseService).listarPorEntrenador(2L);
    }

    @Test
    void clasesDelEntrenador_NoContent() {
        doNothing().when(validator).requireRole(httpServletRequest, "ENTRENADOR");
        when(validator.getUserId(httpServletRequest)).thenReturn(2L);
        when(claseService.listarPorEntrenador(2L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Clase>> response = claseController.clasesDelEntrenador(httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
