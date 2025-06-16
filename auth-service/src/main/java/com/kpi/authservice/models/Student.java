package com.kpi.authservice.models;

import com.kpi.authservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("student")
public class Student extends User {
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private StudentGroup group;

    public Student(String firstName, String lastName, String email, String password, StudentGroup group) {
        super(firstName, lastName, email, password, UserRole.STUDENT);
        this.group = group;
    }
}
