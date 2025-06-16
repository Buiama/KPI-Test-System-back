package com.kpi.authservice.services.interfaces;

import com.kpi.authservice.dtos.requests.LoginRequest;
import com.kpi.authservice.dtos.responses.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ILoginService {
    LoginResponse login(LoginRequest request);
    void refreshJwtToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
