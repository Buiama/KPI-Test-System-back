package com.kpi.authservice.controllers;

import com.kpi.authservice.dtos.responses.StudentGroupResponse;
import com.kpi.authservice.services.interfaces.IStudentGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class StudentGroupController {
    private final IStudentGroupService studentGroupService;

    @GetMapping
    public ResponseEntity<List<StudentGroupResponse>> getAllStudentGroups() {
        return new ResponseEntity<>(studentGroupService.getAllStudentGroups(), HttpStatus.OK);
    }
    
    @GetMapping("/{groupId}/exists")
    public boolean groupExists(@PathVariable Long groupId) {
        return studentGroupService.existsById(groupId);
    }
}
