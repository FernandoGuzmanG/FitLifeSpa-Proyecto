package com.fitlifespa.microservice_auth.dto;

import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class LoginResponse extends RepresentationModel<LoginResponse> {
    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }

}

