package com.kpi.codeexecutionservice.services.interfaces;

import com.kpi.codeexecutionservice.dtos.requests.CreateAssignmentRequest;
import com.kpi.codeexecutionservice.dtos.responses.AssignmentResponse;

import java.util.List;

public interface IAssignmentService {
    AssignmentResponse createAssignment(CreateAssignmentRequest request);
    List<AssignmentResponse> getAllAssignments();
    AssignmentResponse getAssignmentById(Long id);
    List<AssignmentResponse> getAssignmentsByTeacher(String email);
    AssignmentResponse updateAssignment(Long id, CreateAssignmentRequest request);
    void deleteAssignment(Long id);
    boolean isSubmissionAllowedForAssignment(Long assignmentId);
}
