package com.kpi.authservice.services.implementations;

import com.kpi.authservice.dtos.requests.LoginRequest;
import com.kpi.authservice.dtos.responses.LoginResponse;
import com.kpi.authservice.exceptions.StudentNotFoundException;
import com.kpi.authservice.exceptions.WrongPasswordException;
import com.kpi.authservice.models.JwtToken;
import com.kpi.authservice.models.User;
import com.kpi.authservice.repositories.IJwtTokenRepository;
import com.kpi.authservice.repositories.IUserRepository;
import com.kpi.authservice.services.interfaces.IJwtService;
import com.kpi.authservice.services.interfaces.ILoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService implements ILoginService {
    private final IJwtService jwtService;
    private final IUserRepository userRepository;
    private final IJwtTokenRepository jwtTokenRepository;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            throw new WrongPasswordException("Wrong password or email");
        }

        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if(user.isEmpty()) {
            throw new StudentNotFoundException(request.getEmail());
        }

        String jwtToken = jwtService.getJwtToken(user.get());
        String refreshJwtToken = jwtService.generateRefreshJwtToken(user.get());
        revokeAllUserJwtTokens(user.get());
        saveUserJwtToken(user.get(), jwtToken);
        return LoginResponse.builder().accessJwtToken(jwtToken).refreshJwtToken(refreshJwtToken).build();
    }

    private void revokeAllUserJwtTokens(User user) {
        List<JwtToken> validUserTokens = jwtTokenRepository.findAllValidJwtTokensByUser(user.getUserId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jwtTokenRepository.saveAll(validUserTokens);
    }

    private void saveUserJwtToken(User user, String jwtToken) {
        JwtToken jwt = new JwtToken(jwtToken, user);
        jwtTokenRepository.save(jwt);
    }

    public void refreshJwtToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        final String refreshJwtToken = authHeader.substring(7);
        final String email = jwtService.getEmail(refreshJwtToken);
        if (email != null) {
            User user = this.userRepository.findByEmail(email).orElseThrow();
            if (jwtService.isJwtTokenValid(refreshJwtToken, user)) {
                String accessToken = jwtService.getJwtToken(user);
                revokeAllUserJwtTokens(user);
                saveUserJwtToken(user, accessToken);
                LoginResponse authResponse = LoginResponse.builder()
                        .accessJwtToken(accessToken).refreshJwtToken(refreshJwtToken).build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
