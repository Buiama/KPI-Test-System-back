package com.kpi.codeexecutionservice.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kpi.codeexecutionservice.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestResultResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("test_id")
    private Long testId;

    @JsonProperty("test_name")
    private String testName;

    @JsonProperty("status")
    private TestStatus status;

    @JsonProperty("actual_output") // ADDED
    private String actualOutput;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("execution_time_ms")
    private long executionTimeMs;

    @JsonProperty("score_awarded") // Баллы, полученные за этот тест
    private Integer scoreAwarded;
}
