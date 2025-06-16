package com.kpi.authservice.services.interfaces;

import com.kpi.authservice.models.Student;

public interface IRegistrationStudentService {
    String signUp(Student student);
    String resendEmail(Student student);
}
