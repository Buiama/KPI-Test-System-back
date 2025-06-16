package com.kpi.authservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-service")
public interface EmailServiceClient {
    @PostMapping("/api/v1/email/send")
    void sendEmail(@RequestBody EmailRequest request);
    
    record EmailRequest(String to, String email) {}
}
