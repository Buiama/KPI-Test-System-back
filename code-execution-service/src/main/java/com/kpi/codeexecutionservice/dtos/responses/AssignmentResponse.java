package com.kpi.codeexecutionservice.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_by")
    private String createdByEmail;

    @JsonProperty("timeout_seconds")
    private int timeoutSeconds;

    @JsonProperty("memory_limit_mb")
    private long memoryLimitMB;

    @JsonProperty("cpu_limit")
    private int cpuLimit;

    @JsonProperty("allowed_languages")
    private Set<ProgrammingLanguage> allowedLanguages;

    @JsonProperty("allow_multiple_files")
    private boolean allowMultipleFiles;

    @JsonProperty("use_fast_execution_mode")
    private boolean useFastExecutionMode;

    @JsonProperty("deadline")
    private LocalDateTime deadline;

    @JsonProperty("passing_score") // Проходной балл
    private Integer passingScore;

    @JsonProperty("max_submissions") // New field
    private Integer maxSubmissions;

    @JsonProperty("tests") // Добавлено поле для тестов
    private List<TestResponse> tests;
}