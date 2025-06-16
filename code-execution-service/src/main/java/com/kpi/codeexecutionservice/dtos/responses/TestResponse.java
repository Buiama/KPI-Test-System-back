package com.kpi.codeexecutionservice.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("input")
    private String input;

    @JsonProperty("expected_output")
    private String expectedOutput;

    @JsonProperty("is_public")
    private boolean isPublic;

    @JsonProperty("score")
    private Integer score;

    @JsonProperty("assignment_id")
    private Long assignmentId;
}
