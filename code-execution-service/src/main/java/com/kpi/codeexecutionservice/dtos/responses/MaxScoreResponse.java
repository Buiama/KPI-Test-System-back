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
public class MaxScoreResponse {
    @JsonProperty("student_email")
    private String studentEmail;

    @JsonProperty("assignment_id")
    private Long assignmentId;

    @JsonProperty("max_score")
    private Integer maxScore;

    @JsonProperty("assignment_passing_score")
    private Integer assignmentPassingScore;

    @JsonProperty("passed")
    private boolean passed;
}
