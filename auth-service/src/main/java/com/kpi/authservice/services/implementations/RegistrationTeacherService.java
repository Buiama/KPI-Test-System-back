package com.kpi.authservice.services.implementations;

import com.kpi.authservice.dtos.requests.SetPasswordRequest;
import com.kpi.authservice.dtos.requests.TeacherRegistrationRequest;
import com.kpi.authservice.exceptions.*;
import com.kpi.authservice.models.ConfirmationToken;
import com.kpi.authservice.models.StudentGroup;
import com.kpi.authservice.models.Teacher;
import com.kpi.authservice.repositories.IStudentGroupRepository;
import com.kpi.authservice.repositories.ITeacherRepository;
import com.kpi.authservice.repositories.IUserRepository;
import com.kpi.authservice.services.interfaces.IConfirmationTokenService;
import com.kpi.authservice.services.interfaces.IRegistrationTeacherService;
import com.kpi.authservice.services.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RegistrationTeacherService implements IRegistrationTeacherService {
    private static final int TEACHER_TOKEN_VALIDITY_MINUTES = 2880;  // 2 days

    private final IStudentGroupRepository studentGroupRepository;
    private final ITeacherRepository teacherRepository;
    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IConfirmationTokenService confirmationTokenService;
    private final IUserService userService;

    public Set<Long> validateGroups(Set<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return new HashSet<>();
        }
        return studentGroupRepository.findAllById(groupIds).stream()
                .map(StudentGroup::getGroupId)
                .collect(Collectors.toSet());
    }

    @Transactional
    public String registerTeacher(TeacherRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyTakenException();
        }
        Set<Long> validGroupIds = validateGroups(request.getGroupIds());

        String temporaryPassword = UUID.randomUUID().toString();
        Teacher teacher = new Teacher(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                bCryptPasswordEncoder.encode(temporaryPassword),
                validGroupIds
        );
        teacherRepository.save(teacher);

        return userService.generateConfirmationToken(teacher, TEACHER_TOKEN_VALIDITY_MINUTES);
    }

    @Transactional
    public String resendConfirmation(String email) {
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new TeacherNotFoundException(email));
        if (teacher.isEnabled()) {
            throw new EmailAlreadyTakenException("Teacher account is already enabled");
        }

        return userService.generateConfirmationToken(teacher, TEACHER_TOKEN_VALIDITY_MINUTES);
    }

    @Transactional
    public String setPassword(SetPasswordRequest request, String token) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new WrongPasswordException("Password and confirmation password do not match");
        }
        
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));
                
        if (confirmationToken.getConfirmedAt() != null) {
            throw new EmailAlreadyTakenException("Account already confirmed");
        }
        
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }
        
        confirmationTokenService.setConfirmedAt(token);
        
        Teacher teacher = (Teacher) confirmationToken.getUser();
        teacher.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        teacher.setEnabled(true);
        teacherRepository.save(teacher);
        
        return "Password set successfully";
    }
}
