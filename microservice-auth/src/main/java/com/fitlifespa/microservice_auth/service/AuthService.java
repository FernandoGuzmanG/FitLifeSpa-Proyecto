package com.fitlifespa.microservice_auth.service;

import com.fitlifespa.microservice_auth.dto.LoginRequest;
import com.fitlifespa.microservice_auth.dto.LoginResponse;
import com.fitlifespa.microservice_auth.dto.RegisterRequest;
import com.fitlifespa.microservice_auth.hateoas.LoginResponseAssembler;
import com.fitlifespa.microservice_auth.model.Enum.NombreRol;
import com.fitlifespa.microservice_auth.model.EstadoUsuario;
import com.fitlifespa.microservice_auth.model.Rol;
import com.fitlifespa.microservice_auth.model.Usuario;
import com.fitlifespa.microservice_auth.repository.EstadoUsuarioRepository;
import com.fitlifespa.microservice_auth.repository.RolRepository;
import com.fitlifespa.microservice_auth.repository.UsuarioRepository;
import com.fitlifespa.microservice_auth.security.JwtUtil;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EstadoUsuarioRepository estadoUsuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginResponseAssembler loginResponseAssembler;

    public LoginResponse login(LoginRequest request) {
        Usuario user = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.clave(), user.getClave())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        if (!user.isEnabled()) {
        throw new DisabledException("Usuario inactivo");
        }

        String token = jwtUtil.generateToken(user);

        return loginResponseAssembler.toModel(token, user);
    }

    public void register(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("Correo ya registrado");
        }

        if (usuarioRepository.existsByRut(request.rut())){
            throw new IllegalArgumentException("Rut ya registrado");
        }

        if (request.clave().length() < 4 || request.clave().length() > 16){
            throw new BadCredentialsException("La contraseña debe tener entre 4 y 16 carácteres");
        }

        Rol rolCliente = rolRepository.findByNombreRol(NombreRol.CLIENTE)
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));

        EstadoUsuario estadoUsuarioActivo = estadoUsuarioRepository.findByNombreEstado("ACTIVO")
                .orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));

        Usuario nuevo = new Usuario();
        nuevo.setRut(request.rut());
        nuevo.setCorreo(request.correo());
        nuevo.setClave(passwordEncoder.encode(request.clave()));
        nuevo.setPnombre(request.pnombre());
        nuevo.setSnombre(request.snombre());
        nuevo.setAppaterno(request.appaterno());
        nuevo.setApmaterno(request.apmaterno());
        nuevo.setRol(rolCliente);
        nuevo.setEstado(estadoUsuarioActivo);

        usuarioRepository.save(nuevo);
    }
}

