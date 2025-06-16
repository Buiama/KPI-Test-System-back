package com.kpi.authservice.dtos.requests;

import lombok.Data;

@Data
public class SetPasswordRequest {
    private final String password;
    private final String confirmPassword;
}
