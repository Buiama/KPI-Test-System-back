package com.kpi.codeexecutionservice.repositories;

import com.kpi.codeexecutionservice.models.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ITestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByEvaluationId(Long evaluationId);
    List<TestResult> findByTestId(Long testId);
}
