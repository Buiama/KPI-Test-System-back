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
public class ExecutedCodeResponse {
    @JsonProperty("exit_code")
    private int exitCode;
    @JsonProperty("output")
    private String output;
    @JsonProperty("execution_time")
    private long executionTimeMs;
    @JsonProperty("memory_used")
    private long memoryUsedBytes;
    @JsonProperty("error")
    private String error;
}