package com.kpi.codeexecutionservice.services.interfaces;

import com.kpi.codeexecutionservice.dtos.requests.CodeFileRequest;
import com.kpi.codeexecutionservice.dtos.requests.RunCodeRequest;
import com.kpi.codeexecutionservice.dtos.requests.SubmitCodeRequest;
import com.kpi.codeexecutionservice.dtos.responses.CodeSubmissionResponse;
import com.kpi.codeexecutionservice.dtos.responses.ExecutedCodeResponse;
import com.kpi.codeexecutionservice.dtos.responses.TestResultResponse;
import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import com.kpi.codeexecutionservice.models.Assignment;
import com.kpi.codeexecutionservice.models.CodeSubmission;
import com.kpi.codeexecutionservice.models.Test;

import java.util.List;

public interface ICodeService {
    CodeSubmission saveCode(SubmitCodeRequest request);
    List<CodeSubmissionResponse> getAllSubmissions();
    List<CodeSubmissionResponse> getSubmissionsByStudent(String email);
    List<CodeSubmissionResponse> getSubmissionsByAssignment(Long assignmentId);
    List<CodeSubmissionResponse> getSubmissionsByStudentAndAssignment(String email, Long assignmentId);
    CodeSubmissionResponse getSubmissionById(Long id);
    ExecutedCodeResponse executeCode(RunCodeRequest request);
    List<TestResultResponse> executeCodeWithTests(ProgrammingLanguage language, List<CodeFileRequest> files,
                                                  Assignment assignment, List<Test> tests);
}