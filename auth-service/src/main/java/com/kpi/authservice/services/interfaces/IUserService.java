package com.kpi.authservice.services.interfaces;

import com.kpi.authservice.dtos.requests.ChangePasswordRequest;
import com.kpi.authservice.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    void enableUser(String email);
    String generateConfirmationToken(User user, int validityMinutes);
    void changePassword(ChangePasswordRequest request, String userEmail);
}
