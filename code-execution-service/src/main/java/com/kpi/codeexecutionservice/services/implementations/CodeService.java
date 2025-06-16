package com.kpi.codeexecutionservice.services.implementations;

import com.kpi.codeexecutionservice.dtos.requests.RunCodeRequest;
import com.kpi.codeexecutionservice.dtos.requests.SubmitCodeRequest;
import com.kpi.codeexecutionservice.dtos.responses.CodeFileResponse;
import com.kpi.codeexecutionservice.dtos.requests.CodeFileRequest;
import com.kpi.codeexecutionservice.dtos.responses.CodeSubmissionResponse;
import com.kpi.codeexecutionservice.dtos.responses.ExecutedCodeResponse;
import com.kpi.codeexecutionservice.dtos.responses.TestResultResponse;
import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import com.kpi.codeexecutionservice.enums.TestStatus;
import com.kpi.codeexecutionservice.exceptions.DeadlinePassedException;
import com.kpi.codeexecutionservice.exceptions.ExecutionException;
import com.kpi.codeexecutionservice.models.Assignment;
import com.kpi.codeexecutionservice.models.CodeFile;
import com.kpi.codeexecutionservice.models.CodeSubmission;
import com.kpi.codeexecutionservice.models.Test;
import com.kpi.codeexecutionservice.repositories.IAssignmentRepository;
import com.kpi.codeexecutionservice.repositories.ICodeSubmissionRepository;
import com.kpi.codeexecutionservice.services.interfaces.IAssignmentService;
import com.kpi.codeexecutionservice.services.interfaces.ICodeService;
import com.kpi.codeexecutionservice.services.interfaces.IDockerService;
import com.kpi.codeexecutionservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeService implements ICodeService {
    private final ICodeSubmissionRepository submissionRepository;
    private final IAssignmentRepository assignmentRepository;
    private final IDockerService dockerService;
    private final IAssignmentService assignmentService;

    @Override
    @Transactional
    public CodeSubmission saveCode(SubmitCodeRequest request) {
        checkAssignmentDeadline(request.getAssignmentId());
        validateAssignmentConstraints(request);

        CodeSubmission submission = CodeSubmission.builder()
                .language(request.getLanguage())
                .studentEmail(request.getEmail())
                .assignmentId(request.getAssignmentId())
                .submittedAt(LocalDateTime.now())
                .build();

        for (CodeFileRequest fileDto : request.getFiles()) {
            CodeFile file = new CodeFile(
                    fileDto.getFilename(),
                    fileDto.getContent(),
                    fileDto.isMainFile()
            );
            submission.addFile(file);
        }

        return submissionRepository.save(submission);
    }

    @Override
    public List<CodeSubmissionResponse> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CodeSubmissionResponse> getSubmissionsByStudent(String email) {
        return submissionRepository.findByStudentEmail(email).stream()
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CodeSubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId).stream()
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CodeSubmissionResponse> getSubmissionsByStudentAndAssignment(String email, Long assignmentId) {
        return submissionRepository.findByStudentEmailAndAssignmentId(email, assignmentId).stream()
                .map(this::mapToSubmissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CodeSubmissionResponse getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .map(this::mapToSubmissionResponse)
                .orElseThrow(() -> new ExecutionException("Submission not found with id: " + id));
    }

    @Override
    public ExecutedCodeResponse executeCode(RunCodeRequest request) {
        String executionId = UUID.randomUUID().toString();

        try {
            List<Pair<String, String>> files = prepareFiles(request.getLanguage(), request.getFiles());

            long startTime = System.currentTimeMillis();
            CompletableFuture<Pair<Integer, String>> futureResult = dockerService.executeInContainer(
                    executionId,
                    request.getLanguage().getValue(),
                    files,
                    request.getInputs(),
                    0,
                    0,
                    0
            );

            Pair<Integer, String> result = futureResult.get();
            long executionTime = System.currentTimeMillis() - startTime;

            return ExecutedCodeResponse.builder()
                    .exitCode(result.getFirst())
                    .output(result.getSecond())
                    .executionTimeMs(executionTime)
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutionException("Code execution was interrupted", e);
        } catch (ExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutionException("Error executing code: " + e.getMessage(), e);
        } finally {
            dockerService.cleanup(executionId);
        }
    }

    @Override
    public List<TestResultResponse> executeCodeWithTests(ProgrammingLanguage language, List<CodeFileRequest> files,
                                                     Assignment assignment, List<Test> tests) {
        List<TestResultResponse> results = new ArrayList<>();

        for (Test test : tests) {
            TestResultResponse result = executeSingleTest(language, files, assignment, test);
            results.add(result);
        }

        return results;
    }

    public TestResultResponse executeSingleTest(ProgrammingLanguage language, List<CodeFileRequest> userFiles,
                                                Assignment assignment, Test test) {
        String executionId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            List<Pair<String, String>> files = prepareFiles(language, userFiles);
            List<String> testInputs = parseTestInput(test.getInput());

            CompletableFuture<Pair<Integer, String>> futureResult = dockerService.executeInContainer(
                    executionId,
                    language.getValue(),
                    files,
                    testInputs,
                    assignment.getTimeoutSeconds(),
                    assignment.getMemoryLimitMB(),
                    assignment.getCpuLimit(),
                    assignment.getId()
            );

            Pair<Integer, String> result = futureResult.get();
            long executionTime = System.currentTimeMillis() - startTime;

            return createTestResult(test, result, executionTime);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createErrorTestResult(test, "Execution was interrupted", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            return createErrorTestResult(test, "Error executing test: " + e.getMessage(), System.currentTimeMillis() - startTime);
        } finally {
            dockerService.cleanup(executionId);
        }
    }

    private TestResultResponse createTestResult(Test test, Pair<Integer, String> result, long executionTime) {
        int exitCode = result.getFirst();
        String actualOutput = result.getSecond().trim();
        String expectedOutput = test.getExpectedOutput().trim();

        TestStatus status;
        String errorMessage = null;
        Integer scoreAwarded = 0;

        if (exitCode != 0) {
            if (actualOutput.toLowerCase().contains("timeout") || actualOutput.toLowerCase().contains("timed out")) {
                status = TestStatus.TIMEOUT;
                errorMessage = "Execution timed out";
            } else if (actualOutput.toLowerCase().contains("memory") || actualOutput.toLowerCase().contains("out of memory")) {
                status = TestStatus.MEMORY_LIMIT_EXCEEDED;
                errorMessage = "Memory limit exceeded";
            } else {
                status = TestStatus.RUNTIME_ERROR;
                errorMessage = "Runtime error (exit code: " + exitCode + ")";
            }
        } else if (actualOutput.equals(expectedOutput)) {
            status = TestStatus.PASSED;
            if (!test.isPublic() && test.getScore() != null) {
                scoreAwarded = test.getScore();
            }
        } else {
            status = TestStatus.FAILED;
            errorMessage = "Output doesn't match expected result";
        }

        return TestResultResponse.builder()
                .testId(test.getId())
                .testName(test.getName())
                .status(status)
                .actualOutput(actualOutput)
                .errorMessage(errorMessage)
                .executionTimeMs(executionTime)
                .scoreAwarded(scoreAwarded)
                .build();
    }

    private TestResultResponse createErrorTestResult(Test test, String errorMessage, long executionTime) {
        return TestResultResponse.builder()
                .testId(test.getId())
                .testName(test.getName())
                .status(TestStatus.ERROR)
                .actualOutput(null)
                .errorMessage(errorMessage)
                .executionTimeMs(executionTime)
                .scoreAwarded(0)
                .build();
    }

    private List<String> parseTestInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.asList(input.split("\n"));
    }

    private void checkAssignmentDeadline(Long assignmentId) {
        if (assignmentId != null) {
            if (!assignmentService.isSubmissionAllowedForAssignment(assignmentId)) {
                throw new DeadlinePassedException("The deadline for this assignment has passed");
            }
        }
    }

    private void validateAssignmentConstraints(SubmitCodeRequest request) {
        if (request.getAssignmentId() == null) {
            return;
        }

        Assignment assignment = assignmentRepository.findById(request.getAssignmentId()).orElse(null);
        if (assignment != null && !assignment.isAllowMultipleFiles() && request.getFiles().size() > 1) {
            throw new ExecutionException("This assignment does not allow multiple files");
        }
    }

    private List<Pair<String, String>> prepareFiles(ProgrammingLanguage language,
                                                    List<CodeFileRequest> userFiles) {
        List<Pair<String, String>> files = userFiles.stream()
                .map(file -> Pair.of(file.getFilename(), file.getContent()))
                .collect(Collectors.toList());

        boolean hasMainFile = userFiles.stream().anyMatch(CodeFileRequest::isMainFile);
        if (!hasMainFile && !userFiles.isEmpty()) {
            log.warn("No main file specified, using first file as main");
            String mainFileName = FileUtils.getMainFileName(language);
            files.set(0, Pair.of(mainFileName, userFiles.get(0).getContent()));
        }
        else {
            userFiles.stream()
                    .filter(CodeFileRequest::isMainFile)
                    .findFirst()
                    .ifPresent(mainFile -> files.add(Pair.of("main.txt", mainFile.getFilename())));
        }

        return files;
    }

    private CodeSubmissionResponse mapToSubmissionResponse(CodeSubmission submission) {
        List<CodeFileResponse> fileResponses = submission.getFiles().stream()
                .map(file -> CodeFileResponse.builder()
                        .id(file.getId())
                        .filename(file.getFilename())
                        .content(file.getContent())
                        .isMainFile(file.isMainFile())
                        .build())
                .collect(Collectors.toList());

        return CodeSubmissionResponse.builder()
                .id(submission.getId())
                .language(submission.getLanguage())
                .studentEmail(submission.getStudentEmail())
                .assignmentId(submission.getAssignmentId())
                .files(fileResponses)
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
