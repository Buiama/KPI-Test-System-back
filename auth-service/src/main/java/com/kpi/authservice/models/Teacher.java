package com.kpi.authservice.models;

import com.kpi.authservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("teacher")
public class Teacher extends User {
    @ElementCollection
    @CollectionTable(name = "teacher_groups", 
                    joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "group_id")
    private Set<Long> groupIds;
    
    public Teacher(String firstName, String lastName, String email, String password, Set<Long> groupIds) {
        super(firstName, lastName, email, password, UserRole.TEACHER);
        this.groupIds = groupIds;
    }
}
