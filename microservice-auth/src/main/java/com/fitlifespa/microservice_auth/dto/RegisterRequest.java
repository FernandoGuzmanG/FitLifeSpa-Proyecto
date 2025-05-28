package com.fitlifespa.microservice_auth.dto;

// dto/RegisterRequest.java
public record RegisterRequest(String rut, String correo, String clave,
                              String pnombre, String snombre, String appaterno, String apmaterno) {}