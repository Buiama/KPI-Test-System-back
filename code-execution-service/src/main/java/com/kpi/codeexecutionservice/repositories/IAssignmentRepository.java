package com.kpi.codeexecutionservice.repositories;

import com.kpi.codeexecutionservice.models.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface IAssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCreatedByEmail(String email);
}