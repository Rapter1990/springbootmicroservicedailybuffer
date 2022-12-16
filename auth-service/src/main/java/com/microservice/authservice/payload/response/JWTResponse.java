package com.microservice.authservice.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class JWTResponse {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private int id;
    private String username;
    private String email;
    private List<String> roles;
}
