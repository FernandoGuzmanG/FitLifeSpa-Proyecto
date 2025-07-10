package com.fitlifespa.microservice_clases.controller;

import com.fitlifespa.microservice_clases.model.Inscripcion;
import com.fitlifespa.microservice_clases.service.InscripcionService;
import com.fitlifespa.microservice_clases.security.RoleValidator;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

class InscripcionControllerTest {

    @InjectMocks
    private InscripcionController inscripcionController;

    @Mock
    private InscripcionService inscripcionService;

    @Mock
    private RoleValidator validator;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void inscribirse_Success() {
        Long idClase = 1L;
        Long idUsuario = 5L;
        Inscripcion inscripcionMock = new Inscripcion();

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(inscripcionService.inscribirse(idUsuario, idClase)).thenReturn(inscripcionMock);

        ResponseEntity<?> response = inscripcionController.inscribirse(idClase, httpServletRequest);

        assertEquals(CREATED, response.getStatusCode());
        assertEquals(inscripcionMock, response.getBody());

        verify(validator).requireRole(httpServletRequest, "CLIENTE");
        verify(validator).getUserId(httpServletRequest);
        verify(inscripcionService).inscribirse(idUsuario, idClase);
    }

    @Test
    void inscribirse_BadRequest() {
        Long idClase = 1L;

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(5L);
        when(inscripcionService.inscribirse(anyLong(), anyLong()))
            .thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = inscripcionController.inscribirse(idClase, httpServletRequest);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void misInscripciones_Success() {
        Long idUsuario = 5L;
        List<Inscripcion> inscripcionesMock = List.of(new Inscripcion(), new Inscripcion());

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(inscripcionService.inscripcionesDeUsuario(idUsuario)).thenReturn(inscripcionesMock);

        ResponseEntity<List<Inscripcion>> response = inscripcionController.misInscripciones(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(validator).requireRole(httpServletRequest, "CLIENTE");
        verify(validator).getUserId(httpServletRequest);
        verify(inscripcionService).inscripcionesDeUsuario(idUsuario);
    }

    @Test
    void misInscripciones_NoContent() {
        Long idUsuario = 5L;

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(inscripcionService.inscripcionesDeUsuario(idUsuario)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Inscripcion>> response = inscripcionController.misInscripciones(httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void cancelar_Success() {
        Long id = 1L;
        Inscripcion inscripcionMock = new Inscripcion();

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(inscripcionService.cancelarInscripcion(id)).thenReturn(inscripcionMock);

        ResponseEntity<?> response = inscripcionController.cancelar(id, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(inscripcionMock, response.getBody());

        verify(validator).requireRole(httpServletRequest, "CLIENTE");
        verify(inscripcionService).cancelarInscripcion(id);
    }

    @Test
    void cancelar_NotFound() {
        Long id = 1L;

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(inscripcionService.cancelarInscripcion(id))
            .thenThrow(new RuntimeException("No encontrada"));

        ResponseEntity<?> response = inscripcionController.cancelar(id, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    void inscritos_Success() {
        Long idClase = 1L;
        List<Inscripcion> inscripcionesMock = List.of(new Inscripcion(), new Inscripcion());

        doNothing().when(validator).requireRole(httpServletRequest, "ENTRENADOR", "COORDINADOR");
        when(inscripcionService.inscritosEnClase(idClase)).thenReturn(inscripcionesMock);

        ResponseEntity<List<Inscripcion>> response = inscripcionController.inscritos(idClase, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(validator).requireRole(httpServletRequest, "ENTRENADOR", "COORDINADOR");
        verify(inscripcionService).inscritosEnClase(idClase);
    }

    @Test
    void inscritos_NoContent() {
        Long idClase = 1L;

        doNothing().when(validator).requireRole(httpServletRequest, "ENTRENADOR", "COORDINADOR");
        when(inscripcionService.inscritosEnClase(idClase)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Inscripcion>> response = inscripcionController.inscritos(idClase, httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
    }

    @Test
    void historial_Success() {
        Long idUsuario = 5L;
        List<Inscripcion> historialMock = List.of(new Inscripcion(), new Inscripcion());

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(inscripcionService.historialFinalizadas(idUsuario)).thenReturn(historialMock);

        ResponseEntity<List<Inscripcion>> response = inscripcionController.historial(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());

        verify(validator).requireRole(httpServletRequest, "CLIENTE");
        verify(validator).getUserId(httpServletRequest);
        verify(inscripcionService).historialFinalizadas(idUsuario);
    }

    @Test
    void historial_NoContent() {
        Long idUsuario = 5L;

        doNothing().when(validator).requireRole(httpServletRequest, "CLIENTE");
        when(validator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        when(inscripcionService.historialFinalizadas(idUsuario)).thenReturn(Collections.emptyList());

        ResponseEntity<List<Inscripcion>> response = inscripcionController.historial(httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
