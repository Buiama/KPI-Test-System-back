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
public class SubmissionAttemptsInfoResponse {

    @JsonProperty("student_email")
    private String studentEmail;

    @JsonProperty("assignment_id")
    private Long assignmentId;

    @JsonProperty("configured_limit") // Сколько всего разрешено (null если безлимит)
    private Integer configuredLimit;

    @JsonProperty("attempted_count") // Сколько уже сделано отправок (оцененных)
    private long attemptedCount;

    @JsonProperty("remaining_attempts") // Сколько осталось (null если безлимит, или 0 если лимит исчерпан)
    private Integer remainingAttempts;

    @JsonProperty("has_limit") // Есть ли вообще лимит
    private boolean hasLimit;
}