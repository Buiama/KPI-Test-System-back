package com.kpi.codeexecutionservice.services.implementations;

import com.kpi.codeexecutionservice.dtos.requests.TestRequest;
import com.kpi.codeexecutionservice.dtos.responses.TestResponse;
import com.kpi.codeexecutionservice.exceptions.ExecutionException;
import com.kpi.codeexecutionservice.models.Assignment;
import com.kpi.codeexecutionservice.models.Test;
import com.kpi.codeexecutionservice.repositories.IAssignmentRepository;
import com.kpi.codeexecutionservice.repositories.ITestRepository;
import com.kpi.codeexecutionservice.services.interfaces.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService implements ITestService {
    private final ITestRepository testRepository;
    private final IAssignmentRepository assignmentRepository;

    @Override
    @Transactional
    public TestResponse createTest(Long assignmentId, TestRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ExecutionException("Assignment not found with id: " + assignmentId));

        Test test = Test.builder()
                .name(request.getName())
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .isPublic(request.isPublic())
                .score(request.isPublic() ? null : request.getScore())
                .assignment(assignment)
                .build();

        Test savedTest = testRepository.save(test);
        return mapToResponse(savedTest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResponse> getTestsByAssignment(Long assignmentId) {
        return testRepository.findByAssignmentId(assignmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestResponse> getPublicTestsByAssignment(Long assignmentId) {
        return testRepository.findByAssignmentIdAndIsPublic(assignmentId, true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TestResponse getTestById(Long testId) {
        return testRepository.findById(testId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ExecutionException("Test not found with id: " + testId));
    }

    @Override
    @Transactional
    public TestResponse updateTest(Long testId, TestRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ExecutionException("Test not found with id: " + testId));

        test.setName(request.getName());
        test.setInput(request.getInput());
        test.setExpectedOutput(request.getExpectedOutput());
        test.setPublic(request.isPublic());
        test.setScore(request.isPublic() ? null : request.getScore());

        Test updatedTest = testRepository.save(test);
        return mapToResponse(updatedTest);
    }

    @Override
    @Transactional
    public void deleteTest(Long testId) {
        if (!testRepository.existsById(testId)) {
            throw new ExecutionException("Test not found with id: " + testId);
        }
        testRepository.deleteById(testId);
    }

    private TestResponse mapToResponse(Test test) {
        return TestResponse.builder()
                .id(test.getId())
                .name(test.getName())
                .input(test.getInput())
                .expectedOutput(test.getExpectedOutput())
                .isPublic(test.isPublic())
                .score(test.getScore())
                .assignmentId(test.getAssignment().getId())
                .build();
    }
}
