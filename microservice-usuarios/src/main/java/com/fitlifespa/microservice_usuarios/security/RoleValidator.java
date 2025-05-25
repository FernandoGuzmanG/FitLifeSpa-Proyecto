package com.fitlifespa.microservice_usuarios.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RoleValidator {

    public void requireRole(HttpServletRequest request, String... rolesPermitidos) {
        String rolesHeader = request.getHeader("X-User-Roles");

        if (rolesHeader == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se recibió encabezado de roles");
        }

        for (String permitido : rolesPermitidos) {
            if (rolesHeader.contains(permitido)) {
                return;
            }
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para esta acción");
    }
}
