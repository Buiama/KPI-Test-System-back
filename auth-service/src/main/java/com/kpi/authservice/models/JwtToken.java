package com.kpi.authservice.models;

import com.kpi.authservice.enums.JwtTokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jwtTokenId;

    @Column(unique = true)
    private String jwtToken;
    @Enumerated(EnumType.STRING)
    private JwtTokenType jwtTokenType = JwtTokenType.bearer;
    private boolean isRevoked = false;
    private boolean isExpired = false;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    public JwtToken(String jwtToken, User user) {
        this.jwtToken = jwtToken;
        this.user = user;
    }
}
