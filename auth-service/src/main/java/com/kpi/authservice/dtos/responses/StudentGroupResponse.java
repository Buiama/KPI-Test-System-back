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
public class StudentGroupResponse {
    @JsonProperty("id")
    private Long groupId;
    @JsonProperty("code")
    private String code;
}
