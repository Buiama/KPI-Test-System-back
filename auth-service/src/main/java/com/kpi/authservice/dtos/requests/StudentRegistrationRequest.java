package com.kpi.authservice.dtos.requests;

import lombok.Data;

@Data
public class StudentRegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final Long groupId;
}
