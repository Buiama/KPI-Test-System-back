package com.kpi.codeexecutionservice.repositories;

import com.kpi.codeexecutionservice.models.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface IEvaluationRepository extends JpaRepository<Evaluation, Long> {
    
    @Query("SELECT e FROM Evaluation e WHERE e.codeSubmission.studentEmail = :studentEmail")
    List<Evaluation> findByStudentEmail(@Param("studentEmail") String studentEmail);
    
    @Query("SELECT e FROM Evaluation e WHERE e.codeSubmission.assignmentId = :assignmentId")
    List<Evaluation> findByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT e FROM Evaluation e WHERE e.codeSubmission.studentEmail = :studentEmail AND e.codeSubmission.assignmentId = :assignmentId")
    List<Evaluation> findByStudentEmailAndAssignmentId(@Param("studentEmail") String studentEmail, @Param("assignmentId") Long assignmentId);
    
    @Query("SELECT MAX(e.totalScore) FROM Evaluation e WHERE e.codeSubmission.studentEmail = :studentEmail AND e.codeSubmission.assignmentId = :assignmentId")
    Optional<Integer> findMaxScoreByStudentEmailAndAssignmentId(@Param("studentEmail") String studentEmail, @Param("assignmentId") Long assignmentId);
    
    Optional<Evaluation> findByCodeSubmissionId(Long codeSubmissionId);

    long countByCodeSubmissionStudentEmailAndCodeSubmissionAssignmentId(String studentEmail, Long assignmentId);
}
