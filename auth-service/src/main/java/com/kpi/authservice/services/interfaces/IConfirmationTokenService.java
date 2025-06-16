package com.kpi.authservice.services.interfaces;

import com.kpi.authservice.models.ConfirmationToken;

import java.util.Optional;

public interface IConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken confirmationToken);
    Optional<ConfirmationToken> getConfirmationToken(String confirmationToken);
    void setConfirmedAt(String confirmationToken);
}
