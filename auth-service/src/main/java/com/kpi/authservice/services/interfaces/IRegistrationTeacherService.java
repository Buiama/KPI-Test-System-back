package com.kpi.authservice.services.interfaces;

import com.kpi.authservice.dtos.requests.SetPasswordRequest;
import com.kpi.authservice.dtos.requests.TeacherRegistrationRequest;

import java.util.Set;

public interface IRegistrationTeacherService {
    String registerTeacher(TeacherRegistrationRequest request);
    String resendConfirmation(String email);
    String setPassword(SetPasswordRequest request, String token);
    Set<Long> validateGroups(Set<Long> groupIds);
}
