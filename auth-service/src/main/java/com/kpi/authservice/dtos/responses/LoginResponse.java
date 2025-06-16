package com.kpi.authservice.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    @JsonProperty("access_jwt_token")
    private String accessJwtToken;
    @JsonProperty("refresh_jwt_token")
    private String refreshJwtToken;
}
