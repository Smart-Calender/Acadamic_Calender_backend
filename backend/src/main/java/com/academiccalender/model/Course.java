package com.academiccalender.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Course {
    @Id
    private Long id;

    private String courseID;
    private String courseName;
    private int NoOfCredits;
    private int NoOfLabs;

    @JoinColumn (name ="department_id")
    @ManyToOne
    private Department department;

    @ManyToMany
    @JoinTable(
            name = "course_student",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @JsonIgnore
    private List<Student> students;

    @ManyToOne
    @JoinColumn(name = "lab_id")
    private Labs lab;

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "entity_staff",
            joinColumns = @JoinColumn(name = "entity_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    private List<Staff> staff;

}
