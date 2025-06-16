package com.kpi.authservice.dtos.requests;

import lombok.Data;

import java.util.Set;

@Data
public class TeacherRegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Set<Long> groupIds;
}
