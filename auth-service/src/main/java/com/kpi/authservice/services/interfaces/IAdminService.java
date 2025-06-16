package com.kpi.authservice.services.interfaces;

import com.kpi.authservice.dtos.requests.TeacherRegistrationRequest;
import com.kpi.authservice.dtos.responses.TeacherResponse;

import java.util.List;

public interface IAdminService {
    TeacherResponse registerTeacher(TeacherRegistrationRequest request);
    String resendTeacherConfirmation(String teacherEmail);
    List<TeacherResponse> getAllTeachers();
}
