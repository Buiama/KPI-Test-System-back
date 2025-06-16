package com.kpi.authservice.services.interfaces;

import com.kpi.authservice.dtos.responses.StudentGroupResponse;

import java.util.List;

public interface IStudentGroupService {
    List<StudentGroupResponse> getAllStudentGroups();
    boolean existsById(Long groupId);
}
