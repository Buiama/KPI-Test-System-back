package com.kpi.emailservice.controllers;

import com.kpi.emailservice.dtos.requests.EmailRequest;
import com.kpi.emailservice.services.interfaces.IEmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@AllArgsConstructor
public class EmailController {
    private final IEmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {
        emailService.send(request.getTo(), request.getEmail());
        return ResponseEntity.ok().build();
    }
}
