package com.kpi.authservice.controllers;

import com.kpi.authservice.dtos.requests.TeacherRegistrationRequest;
import com.kpi.authservice.dtos.responses.TeacherResponse;
import com.kpi.authservice.services.interfaces.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final IAdminService adminService;

    @PostMapping("/teachers")
    public ResponseEntity<TeacherResponse> registerTeacher(@RequestBody TeacherRegistrationRequest request) {
        return new ResponseEntity<>(adminService.registerTeacher(request),HttpStatus.CREATED);
    }

    @PostMapping("/teachers/{email}/resend-confirmation")
    public ResponseEntity<?> resendTeacherConfirmation(@PathVariable String email) {
        return new ResponseEntity<>(adminService.resendTeacherConfirmation(email), HttpStatus.OK);
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return new ResponseEntity<>(adminService.getAllTeachers(), HttpStatus.OK);
    }
}
