package com.kpi.codeexecutionservice.dtos.requests;

import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class CreateAssignmentRequest {
    private final String title;
    private final String description;
    private final String createdByEmail;
    private final int timeoutSeconds;
    private final long memoryLimitMB;
    private final int cpuLimit;
    private final Set<ProgrammingLanguage> allowedLanguages;
    private final boolean allowMultipleFiles;
    private final boolean useFastExecutionMode;
    private final LocalDateTime deadline;
    private final Integer passingScore;
    private final Integer maxSubmissions;
    private final List<TestRequest> tests;
}