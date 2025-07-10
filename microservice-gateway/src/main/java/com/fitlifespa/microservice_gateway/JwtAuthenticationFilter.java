package com.fitlifespa.microservice_gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final List<String> publicPaths = List.of(
            "/api/auth/login",
            "/api/auth/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        if (publicPaths.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT faltante o inválido");
        }

        String token = authHeader.substring(7);
        Claims claims;

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token JWT inválido");
        }

        String userCorreo = claims.getSubject();
        String roles = claims.get("rol", String.class);

        Long idUsuario;
        Object idRaw = claims.get("idUsuario");
        if (idRaw instanceof Integer) {
            idUsuario = ((Integer) idRaw).longValue();
        } else if (idRaw instanceof Long) {
            idUsuario = (Long) idRaw;
        } else if (idRaw instanceof String) {
            idUsuario = Long.parseLong((String) idRaw);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "idUsuario no válido en token");
        }

        if (userCorreo == null || roles == null || idUsuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token incompleto");
        }

        var request = exchange.getRequest().mutate()
                .header("X-User-Correo", userCorreo)
                .header("X-User-Roles", roles)
                .header("X-User-Id", String.valueOf(idUsuario))
                .build();

        var mutatedExchange = exchange.mutate().request(request).build();
        return chain.filter(mutatedExchange);

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
