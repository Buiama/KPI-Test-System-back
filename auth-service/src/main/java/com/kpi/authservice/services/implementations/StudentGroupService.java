package com.kpi.authservice.services.implementations;

import com.kpi.authservice.dtos.responses.StudentGroupResponse;
import com.kpi.authservice.repositories.IStudentGroupRepository;
import com.kpi.authservice.services.interfaces.IStudentGroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentGroupService implements IStudentGroupService {
    private final IStudentGroupRepository studentGroupRepository;

    public List<StudentGroupResponse> getAllStudentGroups() {
        return studentGroupRepository.findAll().stream()
                .map(group -> new StudentGroupResponse(group.getGroupId(), group.getCode()))
                .collect(Collectors.toList());
    }
    
    public boolean existsById(Long groupId) {
        return studentGroupRepository.existsById(groupId);
    }
}
