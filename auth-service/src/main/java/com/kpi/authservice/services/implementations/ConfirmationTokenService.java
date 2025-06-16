package com.kpi.authservice.services.implementations;

import com.kpi.authservice.models.ConfirmationToken;
import com.kpi.authservice.repositories.IConfirmationTokenRepository;
import com.kpi.authservice.services.interfaces.IConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService implements IConfirmationTokenService {
    private final IConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<ConfirmationToken> getConfirmationToken(String confirmationToken) {
        return confirmationTokenRepository.findByConfirmationToken(confirmationToken);
    }

    public void setConfirmedAt(String confirmationToken) {
        confirmationTokenRepository.updateConfirmedAt(confirmationToken, LocalDateTime.now());
    }
}
