package com.kpi.authservice.repositories;

import com.kpi.authservice.models.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface IConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByConfirmationToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken SET confirmedAt = ?2 WHERE confirmationToken = ?1")
    void updateConfirmedAt(String token, LocalDateTime confirmedAt);
}
