package com.kpi.authservice.services.implementations;

import com.kpi.authservice.models.Student;
import com.kpi.authservice.repositories.IStudentRepository;
import com.kpi.authservice.services.interfaces.IRegistrationStudentService;
import com.kpi.authservice.services.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistrationStudentService implements IRegistrationStudentService {
    private final IUserService userService;
    private final IStudentRepository studentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public String signUp(Student student) {
        String encodedPassword = bCryptPasswordEncoder.encode(student.getPassword());
        student.setPassword(encodedPassword);
        studentRepository.save(student);

        return userService.generateConfirmationToken(student, 15);
    }

    @Transactional
    public String resendEmail(Student student) {
        return userService.generateConfirmationToken(student, 15);
    }
}
