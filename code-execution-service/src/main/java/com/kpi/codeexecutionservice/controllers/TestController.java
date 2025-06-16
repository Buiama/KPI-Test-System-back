package com.kpi.codeexecutionservice.controllers;

import com.kpi.codeexecutionservice.dtos.requests.TestRequest;
import com.kpi.codeexecutionservice.dtos.responses.TestResponse;
import com.kpi.codeexecutionservice.services.interfaces.ITestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
public class TestController {
    private final ITestService testService;

    @PostMapping("/assignment/{assignmentId}")
    public ResponseEntity<TestResponse> createTest(@PathVariable Long assignmentId,
                                                   @RequestBody TestRequest request) {
        return new ResponseEntity<>(testService.createTest(assignmentId, request), HttpStatus.CREATED);
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<TestResponse>> getTestsByAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(testService.getTestsByAssignment(assignmentId));
    }

    @GetMapping("/assignment/{assignmentId}/public")
    public ResponseEntity<List<TestResponse>> getPublicTestsByAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(testService.getPublicTestsByAssignment(assignmentId));
    }

    @GetMapping("/{testId}")
    public ResponseEntity<TestResponse> getTestById(@PathVariable Long testId) {
        return ResponseEntity.ok(testService.getTestById(testId));
    }

    @PutMapping("/{testId}")
    public ResponseEntity<TestResponse> updateTest(@PathVariable Long testId,
                                                   @RequestBody TestRequest request) {
        return ResponseEntity.ok(testService.updateTest(testId, request));
    }

    @DeleteMapping("/{testId}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long testId) {
        testService.deleteTest(testId);
        return ResponseEntity.noContent().build();
    }
}
