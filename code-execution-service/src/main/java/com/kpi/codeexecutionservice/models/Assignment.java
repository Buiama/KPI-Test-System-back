package com.kpi.codeexecutionservice.models;

import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 10000)
    private String description;
    private String createdByEmail;
    private int timeoutSeconds;
    private long memoryLimitMB;
    private int cpuLimit;
    @ElementCollection
    @CollectionTable(name = "assignment_allowed_languages",
            joinColumns = @JoinColumn(name = "assignment_id"))
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Set<ProgrammingLanguage> allowedLanguages = new HashSet<>();
    private boolean allowMultipleFiles;
    private boolean useFastExecutionMode = true;
    private LocalDateTime deadline;
    private Integer passingScore;
    private Integer maxSubmissions;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests = new ArrayList<>();
}
