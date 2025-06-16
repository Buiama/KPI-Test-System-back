package com.kpi.codeexecutionservice.models;

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
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_submission_id", nullable = false, unique = true)
    private CodeSubmission codeSubmission;

    @Column(nullable = false)
    private Integer totalScore;

    @Column(nullable = false)
    private boolean passed;

    @Column(nullable = false)
    private LocalDateTime evaluatedAt;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestResult> testResults = new ArrayList<>();

    public void addTestRunResult(TestResult testResult) {
        testResults.add(testResult);
        testResult.setEvaluation(this);
    }
}
