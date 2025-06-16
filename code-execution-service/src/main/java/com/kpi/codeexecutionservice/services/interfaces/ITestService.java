package com.kpi.codeexecutionservice.services.interfaces;

import com.kpi.codeexecutionservice.dtos.requests.TestRequest;
import com.kpi.codeexecutionservice.dtos.responses.TestResponse;

import java.util.List;

public interface ITestService {
    TestResponse createTest(Long assignmentId, TestRequest request);
    List<TestResponse> getTestsByAssignment(Long assignmentId);
    List<TestResponse> getPublicTestsByAssignment(Long assignmentId);
    TestResponse getTestById(Long testId);
    TestResponse updateTest(Long testId, TestRequest request);
    void deleteTest(Long testId);
}
