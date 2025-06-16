package com.kpi.codeexecutionservice.dtos.requests;

import lombok.Data;

@Data
public class TestRequest {
    private final String name;
    private final String input;
    private final String expectedOutput;
    private final boolean isPublic;
    private final Integer score;
}
