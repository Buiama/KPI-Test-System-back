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
public class CodeFileResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("filename")
    private String filename;
    @JsonProperty("content")
    private String content;
    @JsonProperty("is_main_file")
    private boolean isMainFile;
}