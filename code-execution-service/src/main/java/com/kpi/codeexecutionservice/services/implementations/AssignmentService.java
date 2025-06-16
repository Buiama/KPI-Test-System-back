package com.kpi.codeexecutionservice.services.implementations;

import com.kpi.codeexecutionservice.dtos.requests.CreateAssignmentRequest;
import com.kpi.codeexecutionservice.dtos.requests.TestRequest;
import com.kpi.codeexecutionservice.dtos.responses.AssignmentResponse;
import com.kpi.codeexecutionservice.dtos.responses.TestResponse;
import com.kpi.codeexecutionservice.exceptions.ExecutionException;
import com.kpi.codeexecutionservice.models.Assignment;
import com.kpi.codeexecutionservice.models.Test;
import com.kpi.codeexecutionservice.repositories.IAssignmentRepository;
import com.kpi.codeexecutionservice.services.interfaces.IAssignmentService;
import com.kpi.codeexecutionservice.services.interfaces.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService implements IAssignmentService {
    private final IAssignmentRepository assignmentRepository;
    private final ITestService testService;

    @Override
    @Transactional
    public AssignmentResponse createAssignment(CreateAssignmentRequest request) {
        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdByEmail(request.getCreatedByEmail())
                .timeoutSeconds(request.getTimeoutSeconds())
                .memoryLimitMB(request.getMemoryLimitMB())
                .cpuLimit(request.getCpuLimit())
                .allowedLanguages(request.getAllowedLanguages())
                .allowMultipleFiles(request.isAllowMultipleFiles())
                .useFastExecutionMode(request.isUseFastExecutionMode())
                .deadline(request.getDeadline())
                .passingScore(request.getPassingScore())
                .maxSubmissions(request.getMaxSubmissions())
                .tests(new ArrayList<>())
                .build();

        Assignment savedAssignment = assignmentRepository.save(assignment);

        if (request.getTests() != null && !request.getTests().isEmpty()) {
            for (TestRequest testRequest : request.getTests()) {
                testService.createTest(savedAssignment.getId(), testRequest);
            }
            Assignment reloadedAssignment = assignmentRepository.findById(savedAssignment.getId())
                    .orElseThrow(() -> new ExecutionException(
                            "Failed to reload assignment (ID: " + savedAssignment.getId() + ") after test creation"
                    ));
            return mapToResponse(reloadedAssignment);
        }

        return mapToResponse(savedAssignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ExecutionException("Assignment not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByTeacher(String email) {
        return assignmentRepository.findByCreatedByEmail(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssignmentResponse updateAssignment(Long id, CreateAssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ExecutionException("Assignment not found with id: " + id));

        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setTimeoutSeconds(request.getTimeoutSeconds());
        assignment.setMemoryLimitMB(request.getMemoryLimitMB());
        assignment.setCpuLimit(request.getCpuLimit());
        assignment.setAllowedLanguages(request.getAllowedLanguages());
        assignment.setAllowMultipleFiles(request.isAllowMultipleFiles());
        assignment.setUseFastExecutionMode(request.isUseFastExecutionMode());
        assignment.setDeadline(request.getDeadline());
        assignment.setPassingScore(request.getPassingScore());
        assignment.setMaxSubmissions(request.getMaxSubmissions());

        if (request.getTests() != null) {
            assignment.getTests().clear(); // JPA удалит их из базы при сохранении assignment
            assignmentRepository.saveAndFlush(assignment); // Сохраняем изменения (удаление тестов)

            if (!request.getTests().isEmpty()) {
                for (TestRequest testRequest : request.getTests()) {
                    testService.createTest(assignment.getId(), testRequest);
                }
            }
        }

        Assignment updatedAndReloadedAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ExecutionException("Failed to reload assignment after update"));
        return mapToResponse(updatedAndReloadedAssignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new ExecutionException("Assignment not found with id: " + id);
        }
        // Тесты удалятся каскадно благодаря orphanRemoval = true в Assignment.tests
        assignmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubmissionAllowedForAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ExecutionException("Assignment not found with id: " + assignmentId));

        if (assignment.getDeadline() == null) {
            return true;
        }

        return LocalDateTime.now().isBefore(assignment.getDeadline());
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        // Гарантируем, что коллекция тестов не null перед стримом
        List<Test> tests = assignment.getTests() == null ? Collections.emptyList() : assignment.getTests();

        List<TestResponse> testResponses = tests.stream()
                .map(test -> TestResponse.builder()
                        .id(test.getId())
                        .name(test.getName())
                        // Отправляем input/output только для публичных тестов в общем DTO задания
                        .input(test.isPublic() ? test.getInput() : null)
                        .expectedOutput(test.isPublic() ? test.getExpectedOutput() : null)
                        .isPublic(test.isPublic())
                        .score(test.getScore())
                        .assignmentId(assignment.getId()) // Уже есть в самом assignment, но для консистентности TestResponse
                        .build())
                .collect(Collectors.toList());

        return AssignmentResponse.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .createdByEmail(assignment.getCreatedByEmail())
                .timeoutSeconds(assignment.getTimeoutSeconds())
                .memoryLimitMB(assignment.getMemoryLimitMB())
                .cpuLimit(assignment.getCpuLimit())
                .allowedLanguages(assignment.getAllowedLanguages())
                .allowMultipleFiles(assignment.isAllowMultipleFiles())
                .useFastExecutionMode(assignment.isUseFastExecutionMode())
                .deadline(assignment.getDeadline())
                .passingScore(assignment.getPassingScore())
                .maxSubmissions(assignment.getMaxSubmissions())
                .tests(testResponses)
                .build();
    }
}
