package com.fitlifespa.microservice_servicios.controller;

import com.fitlifespa.microservice_servicios.model.Servicio;
import com.fitlifespa.microservice_servicios.service.ServicioService;
import com.fitlifespa.microservice_servicios.security.RoleValidator;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class ServicioControllerTest {

    @InjectMocks
    private ServicioController servicioController;

    @Mock
    private ServicioService servicioService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearServicio_Success() {
        Servicio entrada = new Servicio();
        entrada.setNombre("Masaje");
        entrada.setDescripcion("Relajante");

        Servicio creado = new Servicio();
        creado.setId(1L);
        creado.setNombre("Masaje");
        creado.setDescripcion("Relajante");

        doNothing().when(validator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(servicioService.crear("Masaje", "Relajante")).thenReturn(creado);

        ResponseEntity<?> response = servicioController.crear(entrada, httpServletRequest);

        assertEquals(CREATED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Servicio creado correctamente"));
        verify(validator).requireRole(httpServletRequest, "ADMINISTRADOR");
        verify(servicioService).crear("Masaje", "Relajante");
    }

    @Test
    void crearServicio_BadRequest() {
        Servicio entrada = new Servicio();
        entrada.setNombre("Masaje");
        entrada.setDescripcion("Relajante");

        doNothing().when(validator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(servicioService.crear("Masaje", "Relajante"))
                .thenThrow(new RuntimeException("Estado ACTIVO no encontrado"));

        ResponseEntity<?> response = servicioController.crear(entrada, httpServletRequest);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Estado ACTIVO no encontrado"));
    }

    @Test
    void listarServicios_Success() {
        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setNombre("Yoga");

        doNothing().when(validator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(servicioService.listarTodos()).thenReturn(List.of(servicio));

        ResponseEntity<?> response = servicioController.listar(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Listado de servicios obtenido correctamente"));
    }

    @Test
    void listarServicios_Forbidden() {
        doThrow(new RuntimeException("No autorizado"))
                .when(validator).requireRole(httpServletRequest, "ADMINISTRADOR");

        ResponseEntity<?> response = servicioController.listar(httpServletRequest);

        assertEquals(FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No autorizado"));
    }

    @Test
    void listarActivos_Success() {
        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setNombre("Yoga");

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        when(servicioService.listarActivos()).thenReturn(List.of(servicio));

        ResponseEntity<?> response = servicioController.listarActivos(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("servicios"));
    }

    @Test
    void obtenerServicio_Success() {
        Servicio servicio = new Servicio();
        servicio.setId(1L);
        servicio.setNombre("Yoga");

        doNothing().when(validator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(servicioService.obtenerPorId(1L)).thenReturn(Optional.of(servicio));

        ResponseEntity<?> response = servicioController.obtener(1L, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void obtenerServicio_NotFound() {
        doNothing().when(validator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(servicioService.obtenerPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = servicioController.obtener(1L, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }
}
