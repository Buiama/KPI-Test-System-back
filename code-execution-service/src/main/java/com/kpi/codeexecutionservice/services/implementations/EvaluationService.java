package com.kpi.codeexecutionservice.services.implementations;

import com.kpi.codeexecutionservice.dtos.requests.CodeFileRequest;
import com.kpi.codeexecutionservice.dtos.requests.SubmitCodeRequest;
import com.kpi.codeexecutionservice.dtos.responses.EvaluationResponse;
import com.kpi.codeexecutionservice.dtos.responses.MaxScoreResponse;
import com.kpi.codeexecutionservice.dtos.responses.SubmissionAttemptsInfoResponse;
import com.kpi.codeexecutionservice.dtos.responses.TestResultResponse;
import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import com.kpi.codeexecutionservice.enums.TestStatus;
import com.kpi.codeexecutionservice.exceptions.ExecutionException;
import com.kpi.codeexecutionservice.exceptions.SubmissionLimitExceededException;
import com.kpi.codeexecutionservice.models.*;
import com.kpi.codeexecutionservice.repositories.IAssignmentRepository;
import com.kpi.codeexecutionservice.repositories.IEvaluationRepository;
import com.kpi.codeexecutionservice.repositories.ITestRepository;
import com.kpi.codeexecutionservice.services.interfaces.ICodeService;
import com.kpi.codeexecutionservice.services.interfaces.IEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationService implements IEvaluationService {
    private final IEvaluationRepository evaluationRepository;
    private final IAssignmentRepository assignmentRepository;
    private final ITestRepository testRepository;
    private final ICodeService codeService;

    @Override
    @Transactional
    public EvaluationResponse submitCodeForEvaluation(SubmitCodeRequest request) {
        // Проверяем, что задание существует
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ExecutionException("Assignment not found with id: " + request.getAssignmentId()));
        validateAssignmentLanguage(assignment, request.getLanguage());
        validateAssignmentFileCount(assignment, request.getFiles());

        if (assignment.getMaxSubmissions() != null) {
            long currentEvaluationsCount = evaluationRepository.countByCodeSubmissionStudentEmailAndCodeSubmissionAssignmentId(
                    request.getEmail(),
                    request.getAssignmentId()
            );
            if (currentEvaluationsCount >= assignment.getMaxSubmissions()) {
                throw new SubmissionLimitExceededException(
                        "Submission limit of " + assignment.getMaxSubmissions() + " for this assignment has been reached."
                );
            }
        }

        CodeSubmission codeSubmission = codeService.saveCode(request);

        List<Test> tests = testRepository.findByAssignmentId(assignment.getId());
        if (tests.isEmpty()) {
            throw new ExecutionException("No tests found for assignment with id: " + assignment.getId());
        }

        List<TestResultResponse> testResults = codeService.executeCodeWithTests(request.getLanguage(),
                request.getFiles(), assignment, tests);

        // Создаем Evaluation
        Evaluation evaluation = createEvaluation(codeSubmission, assignment, testResults);

        // Создаем TestRunResult'ы
        for (int i = 0; i < tests.size(); i++) {
            Test test = tests.get(i);
            TestResultResponse result = testResults.get(i);

            TestResult testResult = TestResult.builder()
                    .test(test)
                    .status(result.getStatus())
                    .actualOutput(result.getActualOutput())
                    .errorMessage(result.getErrorMessage())
                    .executionTimeMs(result.getExecutionTimeMs())
                    .scoreAwarded(result.getScoreAwarded())
                    .build();

            evaluation.addTestRunResult(testResult);
        }

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return mapToEvaluationResponse(savedEvaluation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsByStudent(String studentEmail) {
        return evaluationRepository.findByStudentEmail(studentEmail).stream()
                .map(this::mapToEvaluationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsByAssignment(Long assignmentId) {
        return evaluationRepository.findByAssignmentId(assignmentId).stream()
                .map(this::mapToEvaluationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getEvaluationsByStudentAndAssignment(String studentEmail, Long assignmentId) {
        return evaluationRepository.findByStudentEmailAndAssignmentId(studentEmail, assignmentId).stream()
                .map(this::mapToEvaluationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluationById(Long id) {
        return evaluationRepository.findById(id)
                .map(this::mapToEvaluationResponse)
                .orElseThrow(() -> new ExecutionException("Evaluation not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluationByCodeSubmissionId(Long codeSubmissionId) {
        return evaluationRepository.findByCodeSubmissionId(codeSubmissionId)
                .map(this::mapToEvaluationResponse)
                .orElseThrow(() -> new ExecutionException("Evaluation not found for code submission id: " + codeSubmissionId));
    }

    @Override
    @Transactional(readOnly = true)
    public MaxScoreResponse getMaxScoreByStudentAndAssignment(String studentEmail, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ExecutionException("Assignment not found with id: " + assignmentId));

        Integer maxScore = evaluationRepository.findMaxScoreByStudentEmailAndAssignmentId(studentEmail, assignmentId)
                .orElse(0);

        boolean passed = false;
        if (assignment.getPassingScore() != null) {
            passed = maxScore >= assignment.getPassingScore();
        }

        return MaxScoreResponse.builder()
                .studentEmail(studentEmail)
                .assignmentId(assignmentId)
                .maxScore(maxScore)
                .assignmentPassingScore(assignment.getPassingScore())
                .passed(passed)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionAttemptsInfoResponse getSubmissionAttemptsInfo(String studentEmail, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ExecutionException("Assignment not found with id: " + assignmentId));

        long attemptedCount = evaluationRepository.countByCodeSubmissionStudentEmailAndCodeSubmissionAssignmentId(
                studentEmail,
                assignmentId
        );

        Integer configuredLimit = assignment.getMaxSubmissions();
        boolean hasLimit = configuredLimit != null;
        Integer remainingAttempts = null;

        if (hasLimit) {
            remainingAttempts = Math.max(0, configuredLimit - (int) attemptedCount);
        }

        return SubmissionAttemptsInfoResponse.builder()
                .studentEmail(studentEmail)
                .assignmentId(assignmentId)
                .configuredLimit(configuredLimit)
                .attemptedCount(attemptedCount)
                .remainingAttempts(remainingAttempts)
                .hasLimit(hasLimit)
                .build();
    }

    private void validateAssignmentLanguage(Assignment assignment, ProgrammingLanguage language) {
        if (assignment.getAllowedLanguages() != null && !assignment.getAllowedLanguages().isEmpty()) {
            if (!assignment.getAllowedLanguages().contains(language)) {
                throw new ExecutionException("Language " + language + " is not allowed for this assignment");
            }
        }
    }

    private void validateAssignmentFileCount(Assignment assignment, List<CodeFileRequest> files) {
        if (!assignment.isAllowMultipleFiles() && files.size() > 1) {
            throw new ExecutionException("This assignment does not allow multiple files");
        }
    }

    private Evaluation createEvaluation(CodeSubmission codeSubmission, Assignment assignment, List<TestResultResponse> testResults) {
        int totalScore = testResults.stream()
                .filter(result -> result.getStatus() == TestStatus.PASSED && result.getScoreAwarded() != null)
                .mapToInt(TestResultResponse::getScoreAwarded)
                .sum();

        boolean passed;
        if (assignment.getPassingScore() != null) {
            passed = totalScore >= assignment.getPassingScore();
        } else {
            int passedTestsCount = (int) testResults.stream()
                    .filter(result -> result.getStatus() == TestStatus.PASSED)
                    .count();
            passed = passedTestsCount == testResults.size();
        }

        return Evaluation.builder()
                .codeSubmission(codeSubmission)
                .totalScore(totalScore)
                .passed(passed)
                .evaluatedAt(LocalDateTime.now())
                .testResults(new ArrayList<>())
                .build();
    }

    private EvaluationResponse mapToEvaluationResponse(Evaluation evaluation) {
        List<TestResultResponse> testRunResults = evaluation.getTestResults().stream()
                .map(this::mapToTestResultResponse)
                .collect(Collectors.toList());

        return EvaluationResponse.builder()
                .id(evaluation.getId())
                .codeSubmissionId(evaluation.getCodeSubmission().getId())
                .totalScore(evaluation.getTotalScore())
                .passed(evaluation.isPassed())
                .evaluatedAt(evaluation.getEvaluatedAt())
                .testResults(testRunResults)
                .assignmentId(evaluation.getCodeSubmission().getAssignmentId())
                .studentEmail(evaluation.getCodeSubmission().getStudentEmail())
                .build();
    }

    private TestResultResponse mapToTestResultResponse(TestResult testResult) {
        return TestResultResponse.builder()
                .id(testResult.getId())
                .testId(testResult.getTest().getId())
                .testName(testResult.getTest().getName())
                .status(testResult.getStatus())
                .actualOutput(testResult.getActualOutput())
                .errorMessage(testResult.getErrorMessage())
                .executionTimeMs(testResult.getExecutionTimeMs())
                .scoreAwarded(testResult.getScoreAwarded())
                .build();
    }
}
