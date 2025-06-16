package com.kpi.codeexecutionservice.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
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
public class CodeSubmissionResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("language")
    private ProgrammingLanguage language;
    @JsonProperty("email")
    private String studentEmail;
    @JsonProperty("assignment_id")
    private Long assignmentId;
    @JsonProperty("files")
    private List<CodeFileResponse> files;
    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt;
}