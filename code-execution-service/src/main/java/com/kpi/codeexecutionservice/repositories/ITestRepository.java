package com.kpi.codeexecutionservice.repositories;

import com.kpi.codeexecutionservice.models.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ITestRepository extends JpaRepository<Test, Long> {
    List<Test> findByAssignmentId(Long assignmentId);
    List<Test> findByAssignmentIdAndIsPublic(Long assignmentId, boolean isPublic);
}
