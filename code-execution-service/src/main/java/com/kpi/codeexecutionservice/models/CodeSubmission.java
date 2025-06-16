package com.kpi.codeexecutionservice.models;

import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CodeSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ProgrammingLanguage language;
    @Column(nullable = false)
    private String studentEmail;
    @Column
    private Long assignmentId;
    private LocalDateTime submittedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CodeFile> files = new ArrayList<>();

    public void addFile(CodeFile file) {
        files.add(file);
        file.setSubmission(this);
    }
}