package com.fitlifespa.microservice_usuarios.controller;

import com.fitlifespa.microservice_usuarios.hateoas.UsuarioModelAssembler;
import com.fitlifespa.microservice_usuarios.model.Usuario;
import com.fitlifespa.microservice_usuarios.service.UsuarioService;
import com.fitlifespa.microservice_usuarios.security.RoleValidator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class UsuariosControllerTest {

    @InjectMocks
    private UsuariosController controller;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private RoleValidator roleValidator;

    @Mock
    private UsuarioModelAssembler usuarioModelAssembler;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mostrarUsuarios_Success() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();

        EntityModel<Usuario> m1 = EntityModel.of(u1);
        EntityModel<Usuario> m2 = EntityModel.of(u2);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(usuarioService.findAll()).thenReturn(List.of(u1, u2));
        when(usuarioModelAssembler.toModel(u1)).thenReturn(m1);
        when(usuarioModelAssembler.toModel(u2)).thenReturn(m2);

        ResponseEntity<?> response = controller.mostrarUsuarios(httpServletRequest);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void mostrarUsuarios_NoContent() {
        doNothing().when(roleValidator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(usuarioService.findAll()).thenReturn(List.of());

        ResponseEntity<?> response = controller.mostrarUsuarios(httpServletRequest);

        assertEquals(NO_CONTENT, response.getStatusCode());
    }

    @Test
    void mostrarUsuario_Success() {
        Long id = 1L;
        Usuario u = new Usuario();
        EntityModel<Usuario> model = EntityModel.of(u);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(usuarioService.findById(id)).thenReturn(u);
        when(usuarioModelAssembler.toModel(u)).thenReturn(model);

        ResponseEntity<?> response = controller.mostrarUsuario(id, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void mostrarUsuario_NotFound() {
        Long id = 1L;
        doNothing().when(roleValidator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(usuarioService.findById(id)).thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<?> response = controller.mostrarUsuario(id, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    void buscarPorCorreoUsuario_Success() {
        String correo = "admin@fitlife.com";
        Usuario u = new Usuario();
        EntityModel<Usuario> model = EntityModel.of(u);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(usuarioService.findByCorreo(correo)).thenReturn(u);
        when(usuarioModelAssembler.toModel(u)).thenReturn(model);

        ResponseEntity<?> response = controller.buscarPorCorreoUsuario(correo, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void buscarPorCorreoUsuario_NotFound() {
        String correo = "admin@fitlife.com";

        doNothing().when(roleValidator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(usuarioService.findByCorreo(correo)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = controller.buscarPorCorreoUsuario(correo, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }

    @Test
    void registrarUsuario_Success() {
        Usuario u = new Usuario();
        EntityModel<Usuario> model = EntityModel.of(u);

        doNothing().when(roleValidator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(usuarioService.save(u)).thenReturn(u);
        when(usuarioModelAssembler.toModel(u)).thenReturn(model);

        ResponseEntity<?> response = controller.registrarUsuario(u, httpServletRequest);

        assertEquals(CREATED, response.getStatusCode());
    }

    @Test
    void registrarUsuario_BadRequest() {
        Usuario u = new Usuario();

        doNothing().when(roleValidator).requireRole(httpServletRequest, "ADMINISTRADOR");
        when(usuarioService.save(u)).thenThrow(new RuntimeException());

        ResponseEntity<?> response = controller.registrarUsuario(u, httpServletRequest);

        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void cambiarClave_Success() {
        Map<String, String> body = Map.of(
                "nuevaContrasena", "nueva123",
                "confirmarContrasena", "nueva123"
        );

        Long idUsuario = 5L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        doNothing().when(usuarioService).cambiarClave(idUsuario, "nueva123", "nueva123");

        ResponseEntity<String> response = controller.cambiarClave(body, httpServletRequest);

        assertEquals(OK, response.getStatusCode());
        assertEquals("Contraseña actualizada exitosamente.", response.getBody());
    }

    @Test
    void cambiarClave_BadRequest() {
        Map<String, String> body = Map.of(
                "nuevaContrasena", "x",
                "confirmarContrasena", "y"
        );

        Long idUsuario = 5L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        doThrow(new IllegalArgumentException("No coincide")).when(usuarioService).cambiarClave(idUsuario, "x", "y");

        ResponseEntity<String> response = controller.cambiarClave(body, httpServletRequest);

        assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void cambiarClave_NotFound() {
        Map<String, String> body = Map.of(
                "nuevaContrasena", "pass",
                "confirmarContrasena", "pass"
        );

        Long idUsuario = 5L;

        doNothing().when(roleValidator).requireRole(httpServletRequest, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        when(roleValidator.getUserId(httpServletRequest)).thenReturn(idUsuario);
        doThrow(new EntityNotFoundException()).when(usuarioService).cambiarClave(idUsuario, "pass", "pass");

        ResponseEntity<String> response = controller.cambiarClave(body, httpServletRequest);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }

}
