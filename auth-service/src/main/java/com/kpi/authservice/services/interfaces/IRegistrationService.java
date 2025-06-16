package com.kpi.authservice.services.interfaces;

import com.kpi.authservice.dtos.requests.StudentRegistrationRequest;

public interface IRegistrationService {
    String register(StudentRegistrationRequest request);
    String confirmToken(String token);
}
