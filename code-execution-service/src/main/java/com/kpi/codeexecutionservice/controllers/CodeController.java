package com.kpi.codeexecutionservice.controllers;

import com.kpi.codeexecutionservice.dtos.requests.RunCodeRequest;
import com.kpi.codeexecutionservice.dtos.requests.SubmitCodeRequest;
import com.kpi.codeexecutionservice.dtos.responses.CodeSubmissionResponse;
import com.kpi.codeexecutionservice.dtos.responses.ExecutedCodeResponse;
import com.kpi.codeexecutionservice.models.CodeSubmission;
import com.kpi.codeexecutionservice.services.interfaces.ICodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/code")
@RequiredArgsConstructor
public class CodeController {
    private final ICodeService codeService;

    @PostMapping("/save")
    public ResponseEntity<CodeSubmissionResponse> saveCode(@RequestBody SubmitCodeRequest request) {
        CodeSubmission saved = codeService.saveCode(request);
        return new ResponseEntity<>(codeService.getSubmissionById(saved.getId()), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CodeSubmissionResponse>> getAllSubmissions() {
        return new ResponseEntity<>(codeService.getAllSubmissions(), HttpStatus.OK);
    }

    @GetMapping("/student/{email}")
    public ResponseEntity<List<CodeSubmissionResponse>> getSubmissionsByStudent(@PathVariable String email) {
        return new ResponseEntity<>(codeService.getSubmissionsByStudent(email), HttpStatus.OK);
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<CodeSubmissionResponse>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        return new ResponseEntity<>(codeService.getSubmissionsByAssignment(assignmentId), HttpStatus.OK);
    }

    @GetMapping("/student/{email}/assignment/{assignmentId}")
    public ResponseEntity<List<CodeSubmissionResponse>> getSubmissionsByStudentAndAssignment(
            @PathVariable String email,
            @PathVariable Long assignmentId) {
        return new ResponseEntity<>(
                codeService.getSubmissionsByStudentAndAssignment(email, assignmentId),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodeSubmissionResponse> getSubmissionById(@PathVariable Long id) {
        return new ResponseEntity<>(codeService.getSubmissionById(id), HttpStatus.OK);
    }

    @PostMapping("/run")
    public ResponseEntity<ExecutedCodeResponse> runCode(@RequestBody RunCodeRequest request) {
        return new ResponseEntity<>(codeService.executeCode(request), HttpStatus.OK);
    }
}