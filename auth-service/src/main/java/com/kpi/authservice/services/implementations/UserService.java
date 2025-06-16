package com.kpi.authservice.services.implementations;

import com.kpi.authservice.dtos.requests.ChangePasswordRequest;
import com.kpi.authservice.exceptions.UserNotFoundException;
import com.kpi.authservice.exceptions.WrongPasswordException;
import com.kpi.authservice.models.ConfirmationToken;
import com.kpi.authservice.models.User;
import com.kpi.authservice.repositories.IUserRepository;
import com.kpi.authservice.services.interfaces.IConfirmationTokenService;
import com.kpi.authservice.services.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException(email));
    }

    public String generateConfirmationToken(User user, int validityMinutes) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(validityMinutes), user);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    @Transactional
    public void enableUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        if (!bCryptPasswordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException("Incorrect current password");
        }

        if (!request.getNewPassword().equals(request.getConfirmationNewPassword())) {
            throw new WrongPasswordException("New password and confirmation password do not match");
        }

        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
