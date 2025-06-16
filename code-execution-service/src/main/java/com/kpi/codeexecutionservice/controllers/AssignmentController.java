package com.kpi.codeexecutionservice.controllers;

import com.kpi.codeexecutionservice.dtos.requests.CreateAssignmentRequest;
import com.kpi.codeexecutionservice.dtos.responses.AssignmentResponse;
import com.kpi.codeexecutionservice.services.interfaces.IAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final IAssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<AssignmentResponse> createAssignment(@RequestBody CreateAssignmentRequest request) {
        return new ResponseEntity<>(assignmentService.createAssignment(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    @GetMapping("/teacher/{email}")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByTeacher(@PathVariable String email) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByTeacher(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponse> updateAssignment(@PathVariable Long id,
                                                               @RequestBody CreateAssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
