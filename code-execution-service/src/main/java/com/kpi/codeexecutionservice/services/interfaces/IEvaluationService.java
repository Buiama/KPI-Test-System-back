package com.kpi.codeexecutionservice.services.interfaces;

import com.kpi.codeexecutionservice.dtos.requests.SubmitCodeRequest;
import com.kpi.codeexecutionservice.dtos.responses.EvaluationResponse;
import com.kpi.codeexecutionservice.dtos.responses.MaxScoreResponse;
import com.kpi.codeexecutionservice.dtos.responses.SubmissionAttemptsInfoResponse;

import java.util.List;

public interface IEvaluationService {
    EvaluationResponse submitCodeForEvaluation(SubmitCodeRequest request);
    List<EvaluationResponse> getEvaluationsByStudent(String studentEmail);
    List<EvaluationResponse> getEvaluationsByAssignment(Long assignmentId);
    List<EvaluationResponse> getEvaluationsByStudentAndAssignment(String studentEmail, Long assignmentId);
    EvaluationResponse getEvaluationById(Long id);
    EvaluationResponse getEvaluationByCodeSubmissionId(Long codeSubmissionId);
    MaxScoreResponse getMaxScoreByStudentAndAssignment(String studentEmail, Long assignmentId);
    SubmissionAttemptsInfoResponse getSubmissionAttemptsInfo(String studentEmail, Long assignmentId);
}
