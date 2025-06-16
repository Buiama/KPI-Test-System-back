package com.kpi.codeexecutionservice.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TestStatus {
    PASSED("passed"),
    FAILED("failed"),
    ERROR("error"),
    TIMEOUT("timeout"),
    MEMORY_LIMIT_EXCEEDED("memory_limit_exceeded"),
    RUNTIME_ERROR("runtime_error");

    private final String value;

    TestStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
