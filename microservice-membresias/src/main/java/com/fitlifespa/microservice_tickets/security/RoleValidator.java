package com.fitlifespa.microservice_tickets.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Component
public class RoleValidator {

    public void requireRole(HttpServletRequest request, String... rolesPermitidos) {
        String rolesHeader = request.getHeader("X-User-Roles");

        if (rolesHeader == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se recibió encabezado de roles");
        }

        boolean autorizado = Arrays.stream(rolesPermitidos)
                .anyMatch(rolesHeader::contains);

        if (!autorizado) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para esta acción");
        }
    }
}

