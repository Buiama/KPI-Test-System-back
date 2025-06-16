// src/main/java/com/kpi/authservice/services/implementations/AdminService.java
package com.kpi.authservice.services.implementations;

import com.kpi.authservice.clients.EmailServiceClient;
import com.kpi.authservice.dtos.requests.TeacherRegistrationRequest;
import com.kpi.authservice.dtos.responses.TeacherResponse;
import com.kpi.authservice.exceptions.TeacherNotFoundException;
import com.kpi.authservice.models.Teacher;
import com.kpi.authservice.repositories.ITeacherRepository;
import com.kpi.authservice.services.interfaces.IAdminService;
import com.kpi.authservice.services.interfaces.IRegistrationTeacherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService implements IAdminService {
    private final ITeacherRepository teacherRepository;
    private final IRegistrationTeacherService teacherService;
    private final EmailServiceClient emailServiceClient;

    @Transactional
    public TeacherResponse registerTeacher(TeacherRegistrationRequest request) {
        String token = teacherService.registerTeacher(request);
        sendTeacherConfirmationEmail(request.getEmail(), request.getFirstName(), request.getLastName(), token);

        Teacher teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new TeacherNotFoundException(request.getEmail()));
        return mapToTeacherResponse(teacher);
    }

    @Transactional
    public String resendTeacherConfirmation(String teacherEmail) {
        String token = teacherService.resendConfirmation(teacherEmail);
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(()->new TeacherNotFoundException(teacherEmail));

        sendTeacherConfirmationEmail(teacher.getEmail(), teacher.getFirstName(), teacher.getLastName(), token);
        return token;
    }

    public List<TeacherResponse> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::mapToTeacherResponse)
                .collect(Collectors.toList());
    }

    private void sendTeacherConfirmationEmail(String email, String firstName, String lastName, String token) {
        String link = "http://localhost:9000/api/v1/teachers/confirm?token=" + token;
        emailServiceClient.sendEmail(new EmailServiceClient.EmailRequest(
                email,
                buildTeacherConfirmationEmail(firstName + " " + lastName, link)
        ));
    }

    private TeacherResponse mapToTeacherResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .id(teacher.getUserId())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .email(teacher.getEmail())
                .enabled(teacher.isEnabled())
                .groupIds(teacher.getGroupIds())
                .build();
    }

    private String buildTeacherConfirmationEmail(String name, String link) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Welcome to KPI Test System - Teacher Account</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
                "        <h2>Welcome to KPI Test System!</h2>\n" +
                "        <p>Dear " + name + ",</p>\n" +
                "        <p>An administrator has created a teacher account for you in the KPI Test System. To activate your account, please click the link below to set your password.</p>\n" +
                "        <div style=\"text-align: center; margin: 30px 0;\">\n" +
                "            <a href=\"" + link + "\" style=\"background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Set Password</a>\n" +
                "        </div>\n" +
                "        <p>This link will expire in 48 hours. If you don't set your password within this time, please contact the administrator.</p>\n" +
                "        <p>If you did not expect this invitation, please ignore this email.</p>\n" +
                "        <p>Kind regards,<br>Your KPI Test System Team</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
