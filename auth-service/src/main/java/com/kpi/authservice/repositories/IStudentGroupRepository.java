package com.kpi.authservice.repositories;

import com.kpi.authservice.models.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface IStudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    Optional<StudentGroup> findByCode(String code);
}
