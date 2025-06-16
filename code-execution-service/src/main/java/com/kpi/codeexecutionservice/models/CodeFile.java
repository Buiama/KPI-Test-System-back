package com.kpi.codeexecutionservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CodeFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    @Column(nullable = false, length = 10000)
    private String content;
    private boolean isMainFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private CodeSubmission submission;

    public CodeFile(String filename, String content, boolean isMainFile) {
        this.filename = filename;
        this.content = content;
        this.isMainFile = isMainFile;
    }
}