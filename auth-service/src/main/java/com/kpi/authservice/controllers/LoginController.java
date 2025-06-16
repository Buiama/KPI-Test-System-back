package com.kpi.authservice.controllers;

import com.kpi.authservice.dtos.requests.LoginRequest;
import com.kpi.authservice.services.implementations.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/v1/login")
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return new ResponseEntity<>(loginService.login(request), HttpStatus.OK);
    }

    @PostMapping("/refresh-jwt-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        loginService.refreshJwtToken(request, response);
        return ResponseEntity.ok().build();
    }
}
