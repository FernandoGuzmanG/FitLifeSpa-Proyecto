package com.fitlifespa.microservice_usuarios.service;

import com.fitlifespa.microservice_usuarios.model.Enum.NombreRol;
import com.fitlifespa.microservice_usuarios.model.EstadoUsuario;
import com.fitlifespa.microservice_usuarios.model.Rol;
import com.fitlifespa.microservice_usuarios.model.Usuario;
import com.fitlifespa.microservice_usuarios.repository.EstadoUsuarioRepository;
import com.fitlifespa.microservice_usuarios.repository.RolRepository;
import com.fitlifespa.microservice_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private EstadoUsuarioRepository estadoUsuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_usuarioExiste() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.findById(1L);

        assertEquals(1L, result.getIdUsuario());
    }

    @Test
    void testFindById_usuarioNoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> usuarioService.findById(1L));
        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    void testSave_usuarioConClaveValida_retornaUsuarioGuardado() {
        Usuario usuario = new Usuario();
        usuario.setClave("valida123"); // Longitud 9

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setIdUsuario(1L);

        when(passwordEncoder.encode("valida123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario result = usuarioService.save(usuario);

        assertNotNull(result);
        assertEquals(1L, result.getIdUsuario());
        verify(passwordEncoder).encode("valida123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testSave_usuarioConClaveMuyCorta_retornaNull() {
        Usuario usuario = new Usuario();
        usuario.setClave("123"); // Longitud 3 → inválida

        Usuario result = usuarioService.save(usuario);

        assertNull(result);
        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void testSave_usuarioConClaveMuyLarga_retornaNull() {
        Usuario usuario = new Usuario();
        usuario.setClave("a".repeat(20)); // Longitud 20 → inválida

        Usuario result = usuarioService.save(usuario);

        assertNull(result);
        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void testCambiarClave_correcto() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setClave("oldPass");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPass")).thenReturn("hashedPass");

        usuarioService.cambiarClave(1L, "newPass", "newPass");

        verify(usuarioRepository).save(usuario);
        assertEquals("hashedPass", usuario.getClave());
    }

    @Test
    void testCambiarClave_contraseñasNoCoinciden() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                usuarioService.cambiarClave(1L, "pass1", "pass2"));

        assertEquals("Las contraseñas no coinciden", exception.getMessage());
    }

    @Test
    void testActivarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        EstadoUsuario estado = new EstadoUsuario();
        estado.setNombreEstado("ACTIVO");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(estadoUsuarioRepository.findByNombre("ACTIVO")).thenReturn(estado);

        Usuario result = usuarioService.activarUsuario(1L);

        assertEquals("ACTIVO", result.getEstado().getNombreEstado());
    }

    @Test
    void testActivarUsuario_usuarioNoExiste_lanzaRuntimeException() {
        Long id = 99L;

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.desactivarUsuario(id);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado con ID: " + id));
        verify(usuarioRepository).findById(id);
        verify(estadoUsuarioRepository, never()).findByNombre(any());
    }

    @Test
    void testDesactivarUsuario_usuarioExistente_estadoCambiadoAInactivo() {
        Long id = 1L;
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(id);
        EstadoUsuario inactivo = new EstadoUsuario();
        inactivo.setNombreEstado("INACTIVO");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(estadoUsuarioRepository.findByNombre("INACTIVO")).thenReturn(inactivo);

        Usuario result = usuarioService.desactivarUsuario(id);

        assertNotNull(result);
        assertEquals("INACTIVO", result.getEstado().getNombreEstado());
        verify(usuarioRepository).findById(id);
        verify(estadoUsuarioRepository).findByNombre("INACTIVO");
    }

    @Test
    void testDesactivarUsuario_usuarioNoExiste_lanzaRuntimeException() {
        Long id = 99L;

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.desactivarUsuario(id);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado con ID: " + id));
        verify(usuarioRepository).findById(id);
        verify(estadoUsuarioRepository, never()).findByNombre(any());
    }


    @Test
    void testCambiarRol() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        Rol rol = new Rol();
        rol.setNombreRol(NombreRol.CLIENTE);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(rol);

        Usuario result = usuarioService.cambiarRol(1L, "cliente");

        assertEquals(NombreRol.CLIENTE, result.getRol().getNombreRol());
    }
}

