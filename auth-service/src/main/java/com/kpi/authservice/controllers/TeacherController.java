package com.kpi.authservice.controllers;

import com.kpi.authservice.dtos.requests.SetPasswordRequest;
import com.kpi.authservice.services.interfaces.IRegistrationTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final IRegistrationTeacherService teacherService;
    
    @PostMapping("/confirm")
    public ResponseEntity<?> setPassword(@RequestParam("token") String token, @RequestBody SetPasswordRequest request) {
        return new ResponseEntity<>(teacherService.setPassword(request, token), HttpStatus.OK);
    }
}
