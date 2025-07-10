package com.fitlifespa.microservice_auth.service;

import com.fitlifespa.microservice_auth.dto.LoginRequest;
import com.fitlifespa.microservice_auth.dto.LoginResponse;
import com.fitlifespa.microservice_auth.dto.RegisterRequest;
import com.fitlifespa.microservice_auth.model.Enum.NombreRol;
import com.fitlifespa.microservice_auth.model.EstadoUsuario;
import com.fitlifespa.microservice_auth.model.Rol;
import com.fitlifespa.microservice_auth.model.Usuario;
import com.fitlifespa.microservice_auth.repository.EstadoUsuarioRepository;
import com.fitlifespa.microservice_auth.repository.RolRepository;
import com.fitlifespa.microservice_auth.repository.UsuarioRepository;
import com.fitlifespa.microservice_auth.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fitlifespa.microservice_auth.hateoas.LoginResponseAssembler;


class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private EstadoUsuarioRepository estadoUsuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginResponseAssembler loginResponseAssembler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginSuccessful() {
        LoginRequest request = new LoginRequest("juan@example.com", "clave123");

        Rol rol = new Rol(1L, NombreRol.CLIENTE, null);
        EstadoUsuario estado = new EstadoUsuario(1L, "ACTIVO", null);
        Usuario usuario = new Usuario(1L, "12.345.678-9", "Juan", "Carlos", "Pérez", "González",
                "juan@example.com", "hashed", rol, estado);

        when(usuarioRepository.findByCorreo(request.correo())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.clave(), usuario.getClave())).thenReturn(true);
        when(jwtUtil.generateToken(usuario)).thenReturn("fake-jwt-token");
        when(loginResponseAssembler.toModel("fake-jwt-token", usuario))
                .thenReturn(new LoginResponse("fake-jwt-token"));

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        verify(usuarioRepository).findByCorreo(request.correo());
        verify(passwordEncoder).matches(request.clave(), usuario.getClave());
        verify(jwtUtil).generateToken(usuario);
        verify(loginResponseAssembler).toModel("fake-jwt-token", usuario);
    }

    @Test
    void loginUsuarioNoEncontrado() {
        LoginRequest request = new LoginRequest("noexiste@example.com", "clave123");
        when(usuarioRepository.findByCorreo(request.correo())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
        verify(usuarioRepository).findByCorreo(request.correo());
    }

    @Test
    void loginClaveIncorrecta() {
        LoginRequest request = new LoginRequest("juan@example.com", "claveIncorrecta");

        Usuario usuario = new Usuario();
        usuario.setCorreo("juan@example.com");
        usuario.setClave("hashed");

        when(usuarioRepository.findByCorreo(request.correo())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.clave(), usuario.getClave())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(usuarioRepository).findByCorreo(request.correo());
        verify(passwordEncoder).matches(request.clave(), usuario.getClave());
    }

    @Test
    void loginUsuarioInactivo() {
        LoginRequest request = new LoginRequest("juan@example.com", "clave123");

        Usuario usuario = new Usuario();
        usuario.setCorreo("juan@example.com");
        usuario.setClave("hashed");
        usuario.setEstado(new EstadoUsuario(1L, "INACTIVO", null));
        usuario.setRol(new Rol(1L, NombreRol.CLIENTE, null));

        when(usuarioRepository.findByCorreo(request.correo())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.clave(), usuario.getClave())).thenReturn(true);

        assertThrows(DisabledException.class, () -> authService.login(request));

        verify(usuarioRepository).findByCorreo(request.correo());
        verify(passwordEncoder).matches(request.clave(), usuario.getClave());
    }


    @Test
    void registerSuccessful() {
        RegisterRequest request = new RegisterRequest(
                "12.345.678-9",
                "nuevo@example.com",
                "clave123",
                "Juan",
                "Carlos",
                "Pérez",
                "González"
        );

        when(usuarioRepository.existsByCorreo(request.correo())).thenReturn(false);
        when(rolRepository.findByNombreRol(NombreRol.CLIENTE))
                .thenReturn(Optional.of(new Rol(1L, NombreRol.CLIENTE, null)));
        when(estadoUsuarioRepository.findByNombreEstado("ACTIVO"))
                .thenReturn(Optional.of(new EstadoUsuario(1L, "ACTIVO", null)));
        when(passwordEncoder.encode(request.clave())).thenReturn("hashed");

        authService.register(request);

        verify(usuarioRepository).existsByCorreo(request.correo());
        verify(passwordEncoder).encode(request.clave());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registerCorreoYaExiste() {
        RegisterRequest request = new RegisterRequest(
                "12.345.678-9",
                "duplicado@example.com",
                "clave123",
                "Juan",
                "Carlos",
                "Pérez",
                "González"
        );

        when(usuarioRepository.existsByCorreo(request.correo())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        verify(usuarioRepository).existsByCorreo(request.correo());
    }

    @Test
    void registerRutYaExiste() {
        RegisterRequest request = new RegisterRequest(
                "12.345.678-9",
                "ejemplo@example.com",
                "clave123",
                "Juan",
                "Carlos",
                "Pérez",
                "González"
        );

        when(usuarioRepository.existsByRut(request.rut())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        verify(usuarioRepository).existsByRut(request.rut());
    }

    @Test
    void registerClaveMuyCorta() {
        RegisterRequest request = new RegisterRequest(
                "12.345.678-9",
                "nuevo@example.com",
                "abc",
                "Juan",
                "Carlos",
                "Pérez",
                "González"
        );

        when(usuarioRepository.existsByCorreo(request.correo())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.register(request));

        verify(usuarioRepository).existsByCorreo(request.correo());
    }
}

