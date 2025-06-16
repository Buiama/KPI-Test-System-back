package com.kpi.codeexecutionservice.dtos.requests;

import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import lombok.Data;

import java.util.List;

@Data
public class RunCodeRequest {
    private final ProgrammingLanguage language;
    private final List<CodeFileRequest> files;
    private final List<String> inputs;
}