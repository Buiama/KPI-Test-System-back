package com.kpi.codeexecutionservice.dtos.requests;

import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import lombok.Data;

import java.util.List;

@Data
public class SubmitCodeRequest {
    private final ProgrammingLanguage language;
    private final List<CodeFileRequest> files;
    private final String email;
    private final Long assignmentId;
}