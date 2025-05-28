package com.fitlifespa.microservice_clases.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Component
public class RoleValidator {

    public void requireRole(HttpServletRequest request, String... rolesPermitidos) {
        String rol = request.getHeader("X-User-Roles");
        if (rol == null || Arrays.stream(rolesPermitidos).noneMatch(rol::contains)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a este recurso");
        }
    }

    public Long getUserId(HttpServletRequest request) {
        String id = request.getHeader("X-User-Id");
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ID de usuario no encontrado");
        return Long.parseLong(id);
    }
}

