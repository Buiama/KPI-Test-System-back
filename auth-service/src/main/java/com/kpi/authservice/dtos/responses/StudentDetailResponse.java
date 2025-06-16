// auth-service/src/main/java/com/kpi/authservice/dtos/responses/StudentDetailResponse.java
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
public class StudentDetailResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("group")
    private StudentGroupResponse group;
    @JsonProperty("enabled")
    private boolean isEnabled;
}