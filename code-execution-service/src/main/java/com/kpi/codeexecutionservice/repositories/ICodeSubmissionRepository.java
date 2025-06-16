package com.kpi.codeexecutionservice.repositories;

import com.kpi.codeexecutionservice.models.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ICodeSubmissionRepository extends JpaRepository<CodeSubmission, Long> {
    List<CodeSubmission> findByStudentEmail(String studentEmail);
    List<CodeSubmission> findByAssignmentId(Long assignmentId);
    List<CodeSubmission> findByStudentEmailAndAssignmentId(String studentEmail, Long assignmentId);
}