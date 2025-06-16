package com.kpi.codeexecutionservice.controllers;

import com.kpi.codeexecutionservice.dtos.requests.SubmitCodeRequest;
import com.kpi.codeexecutionservice.dtos.responses.*;
import com.kpi.codeexecutionservice.services.interfaces.IEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluation")
@RequiredArgsConstructor
public class EvaluationController {
    private final IEvaluationService evaluationService;

    @PostMapping("/submit")
    public ResponseEntity<EvaluationResponse> submitCodeForEvaluation(@RequestBody SubmitCodeRequest request) {
        return new ResponseEntity<>(evaluationService.submitCodeForEvaluation(request), HttpStatus.CREATED);
    }

    @GetMapping("/student/{email}")
    public ResponseEntity<List<EvaluationResponse>> getEvaluationsByStudent(@PathVariable String email) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByStudent(email));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<EvaluationResponse>> getEvaluationsByAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByAssignment(assignmentId));
    }

    @GetMapping("/student/{email}/assignment/{assignmentId}")
    public ResponseEntity<List<EvaluationResponse>> getEvaluationsByStudentAndAssignment(
            @PathVariable String email,
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByStudentAndAssignment(email, assignmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationResponse> getEvaluationById(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.getEvaluationById(id));
    }

    @GetMapping("/submission/{codeSubmissionId}")
    public ResponseEntity<EvaluationResponse> getEvaluationByCodeSubmissionId(@PathVariable Long codeSubmissionId) {
        return ResponseEntity.ok(evaluationService.getEvaluationByCodeSubmissionId(codeSubmissionId));
    }

    @GetMapping("/max-score/student/{email}/assignment/{assignmentId}")
    public ResponseEntity<MaxScoreResponse> getMaxScoreByStudentAndAssignment(
            @PathVariable String email,
            @PathVariable Long assignmentId) {
        return ResponseEntity.ok(evaluationService.getMaxScoreByStudentAndAssignment(email, assignmentId));
    }

    @GetMapping("/assignments/{assignmentId}/students/{studentEmail}/attempts-info")
    public ResponseEntity<SubmissionAttemptsInfoResponse> getSubmissionAttemptsInfo(
            @PathVariable Long assignmentId,
            @PathVariable String studentEmail) {
        return ResponseEntity.ok(evaluationService.getSubmissionAttemptsInfo(studentEmail, assignmentId));
    }
}