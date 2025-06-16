package com.kpi.codeexecutionservice.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("code_submission_id")
    private Long codeSubmissionId;

    @JsonProperty("total_score")
    private Integer totalScore;

    @JsonProperty("passed")
    private boolean passed;

    @JsonProperty("evaluated_at")
    private LocalDateTime evaluatedAt;

    @JsonProperty("test_results")
    private List<TestResultResponse> testResults;

    @JsonProperty("assignment_id")
    private Long assignmentId;

    @JsonProperty("student_email")
    private String studentEmail;
}
