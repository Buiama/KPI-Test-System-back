package com.kpi.codeexecutionservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kpi.codeexecutionservice.exceptions.ExecutionException;

import java.util.Arrays;

public enum ProgrammingLanguage {
    JAVA("java"),
    PYTHON("python"),
    CPP("cpp"),
    CSHARP("csharp"),
    JAVASCRIPT("javascript"),
    TYPESCRIPT("typescript"),
    GO("go"),
    KOTLIN("kotlin"),
    JULIA("julia");

    private final String value;

    ProgrammingLanguage(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProgrammingLanguage fromValue(String value) {
        return Arrays.stream(values())
                .filter(language -> language.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ExecutionException("Unsupported programming language: " + value));
    }
}
