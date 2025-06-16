package com.kpi.authservice.services.interfaces;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface IJwtService {
    String getEmail(String jwtToken);
    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);
    String getJwtToken(UserDetails userDetails);
    String getJwtToken(Map<String, Object> extraClaims, UserDetails userDetails);
    String generateRefreshJwtToken(UserDetails userDetails);
    boolean isJwtTokenValid(String jwtToken, UserDetails userDetails);
}
