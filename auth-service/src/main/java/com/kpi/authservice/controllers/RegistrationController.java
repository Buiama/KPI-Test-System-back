package com.kpi.authservice.controllers;

import com.kpi.authservice.dtos.requests.StudentRegistrationRequest;
import com.kpi.authservice.services.implementations.StudentRegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {
    private final StudentRegistrationService studentRegistrationService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody StudentRegistrationRequest request) {
        return new ResponseEntity<>(studentRegistrationService.register(request), HttpStatus.OK);
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        return new ResponseEntity<>(studentRegistrationService.confirmToken(token), HttpStatus.OK);
    }
}
