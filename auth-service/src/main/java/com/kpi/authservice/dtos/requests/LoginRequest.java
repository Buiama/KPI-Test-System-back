package com.kpi.authservice.dtos.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private final String email;
    private final String password;
}
