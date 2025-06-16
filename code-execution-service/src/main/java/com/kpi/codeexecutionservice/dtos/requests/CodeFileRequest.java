package com.kpi.codeexecutionservice.dtos.requests;

import lombok.Data;

@Data
public class CodeFileRequest {
    private final String filename;
    private final String content;
    private final boolean isMainFile;
}