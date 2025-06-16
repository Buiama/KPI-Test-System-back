package com.kpi.authservice.services.implementations;

import com.kpi.authservice.models.JwtToken;
import com.kpi.authservice.repositories.IJwtTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final IJwtTokenRepository jwtTokenRepository;

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        final String jwtToken = authHeader.substring(7);
        Optional<JwtToken> storedToken = jwtTokenRepository.findByJwtToken(jwtToken);
        if (storedToken.isPresent()) {
            storedToken.get().setExpired(true);
            storedToken.get().setRevoked(true);
            jwtTokenRepository.save(storedToken.get());
            SecurityContextHolder.clearContext();
        }
    }
}
